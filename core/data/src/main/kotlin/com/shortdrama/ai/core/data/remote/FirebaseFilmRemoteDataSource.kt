package com.shortdrama.ai.core.data.remote

import android.content.Context
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.shortdrama.ai.core.common.Logger
import com.shortdrama.ai.core.data.BuildConfig
import com.shortdrama.ai.core.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFilmRemoteDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    @ApplicationContext private val context: Context,
) : FilmRemoteDataSource {

    private val initMutex = Mutex()

    @Volatile
    private var initialized = false

    private val defaultCatalogJson: String by lazy {
        context.resources.openRawResource(R.raw.default_film_catalog)
            .bufferedReader()
            .use { it.readText() }
    }

    override suspend fun getCatalogJson(): String {
        ensureInitialized()
        return remoteConfig.getString(RemoteConfigKeys.FILM_CATALOG)
    }

    override suspend fun getDefaultCatalogJson(): String = defaultCatalogJson

    override suspend fun fetchAndActivate(): Boolean {
        ensureInitialized()
        return remoteConfig.fetchAndActivate().await()
    }

    override fun observeCatalogUpdates(): Flow<Unit> = callbackFlow {
        val registration = remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (RemoteConfigKeys.FILM_CATALOG !in configUpdate.updatedKeys) return
                remoteConfig.activate().addOnCompleteListener { trySend(Unit) }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                // Real-time updates are best effort; pull-to-refresh still works.
                Logger.e("Remote Config real-time update failed", error)
            }
        })
        awaitClose { registration.remove() }
    }

    /** Settings and defaults are applied asynchronously; wait for them before the first read. */
    private suspend fun ensureInitialized() {
        if (initialized) return
        initMutex.withLock {
            if (initialized) return
            remoteConfig.setConfigSettingsAsync(
                remoteConfigSettings {
                    // Debug builds always hit the server so console changes show up immediately.
                    minimumFetchIntervalInSeconds =
                        if (BuildConfig.DEBUG) 0 else RELEASE_FETCH_INTERVAL_SECONDS
                },
            ).await()
            remoteConfig.setDefaultsAsync(
                mapOf(RemoteConfigKeys.FILM_CATALOG to defaultCatalogJson),
            ).await()
            initialized = true
        }
    }

    private companion object {
        const val RELEASE_FETCH_INTERVAL_SECONDS = 3600L
    }
}
