package com.shortdrama.ai.core.domain.repository

import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.model.FilmCatalog
import kotlinx.coroutines.flow.Flow

/**
 * Source of truth for films. The domain layer only knows this contract; the Firebase
 * Remote Config implementation lives in :core:data.
 */
interface FilmRepository {

    /** Emits the current catalog immediately (cached/default values), then on every change. */
    fun observeCatalog(): Flow<FilmCatalog>

    /** Fetches the latest catalog from the backend and activates it. */
    suspend fun refresh(): Result<Unit>

    suspend fun getFilm(id: String): Film?
}
