package com.shortdrama.ai.core.data.remote

import kotlinx.coroutines.flow.Flow

interface FilmRemoteDataSource {

    /** Raw catalog JSON from the active config (or the in-app default before the first fetch). */
    suspend fun getCatalogJson(): String

    /** The catalog bundled in the APK, used when the remote value is broken. */
    suspend fun getDefaultCatalogJson(): String

    /** Fetches from the server and activates. Returns true when new values were activated. */
    suspend fun fetchAndActivate(): Boolean

    /** Emits each time a real-time config update touching the catalog has been activated. */
    fun observeCatalogUpdates(): Flow<Unit>
}
