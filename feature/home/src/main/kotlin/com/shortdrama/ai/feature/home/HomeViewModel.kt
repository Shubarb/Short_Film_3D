package com.shortdrama.ai.feature.home

import androidx.lifecycle.viewModelScope
import com.shortdrama.ai.core.domain.usecase.BuildHomeFeedUseCase
import com.shortdrama.ai.core.domain.usecase.ObserveFilmCatalogUseCase
import com.shortdrama.ai.core.domain.usecase.RefreshFilmCatalogUseCase
import com.shortdrama.ai.core.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeFilmCatalog: ObserveFilmCatalogUseCase,
    private val refreshFilmCatalog: RefreshFilmCatalogUseCase,
    private val buildHomeFeed: BuildHomeFeedUseCase,
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    init {
        observeFilmCatalog()
            .onEach { catalog ->
                setState {
                    val feed = buildHomeFeed(catalog.films)
                    copy(
                        isLoading = false,
                        feed = feed,
                        // Keep the banner on screen if it still exists in the new catalog.
                        bannerPosition = bannerPosition.coerceIn(0, (feed.banners.size - 1).coerceAtLeast(0)),
                    )
                }
            }
            .launchIn(viewModelScope)

        // Cached/default films show right away; fetch the latest catalog in the background.
        refresh(userInitiated = false)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> refresh(userInitiated = true)
            is HomeIntent.SelectTab -> setState { copy(selectedTab = intent.tab) }
            is HomeIntent.BannerChanged -> setState { copy(bannerPosition = intent.position) }
            is HomeIntent.OpenFilm -> sendEffect(HomeEffect.NavigateToPlayer(intent.filmId))
        }
    }

    private fun refresh(userInitiated: Boolean) {
        viewModelScope.launch {
            if (userInitiated) setState { copy(isRefreshing = true) }
            refreshFilmCatalog().onFailure {
                if (userInitiated) sendEffect(HomeEffect.ShowRefreshError)
            }
            setState { copy(isRefreshing = false) }
        }
    }
}
