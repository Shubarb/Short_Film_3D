package com.shortdrama.ai.core.data.repository

import com.shortdrama.ai.core.common.Logger
import com.shortdrama.ai.core.common.dispatcher.AppDispatcher
import com.shortdrama.ai.core.common.dispatcher.Dispatcher
import com.shortdrama.ai.core.data.parser.FilmCatalogParser
import com.shortdrama.ai.core.data.remote.FilmRemoteDataSource
import com.shortdrama.ai.core.domain.repository.FilmRepository
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.model.FilmCatalog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilmRepositoryImpl @Inject constructor(
    private val remoteDataSource: FilmRemoteDataSource,
    private val parser: FilmCatalogParser,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : FilmRepository {

    /** Bumped after every successful fetch so collectors re-read the active config. */
    private val activations = MutableStateFlow(0)

    override fun observeCatalog(): Flow<FilmCatalog> =
        merge(
            activations.map { },
            remoteDataSource.observeCatalogUpdates(),
        )
            .map { readCatalog() }
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun refresh(): Result<Unit> = withContext(ioDispatcher) {
        try {
            remoteDataSource.fetchAndActivate()
            activations.update { it + 1 }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e("Failed to fetch film catalog", e)
            Result.failure(e)
        }
    }

    override suspend fun getFilm(id: String): Film? = withContext(ioDispatcher) {
        readCatalog().films.firstOrNull { it.id == id }
    }

    private suspend fun readCatalog(): FilmCatalog =
        try {
            parser.parse(remoteDataSource.getCatalogJson())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // A typo in the console must not blank the app: fall back to the bundled catalog.
            Logger.e("Invalid film catalog JSON in Remote Config, using bundled default", e)
            runCatching { parser.parse(remoteDataSource.getDefaultCatalogJson()) }
                .getOrDefault(FilmCatalog.EMPTY)
        }
}
