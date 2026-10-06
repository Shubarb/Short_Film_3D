package com.shortdrama.ai.core.domain.usecase

import com.shortdrama.ai.core.domain.repository.FilmRepository
import com.shortdrama.ai.core.model.FilmCatalog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFilmCatalogUseCase @Inject constructor(
    private val repository: FilmRepository,
) {
    operator fun invoke(): Flow<FilmCatalog> = repository.observeCatalog()
}
