package com.shortdrama.ai.feature.home

import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.model.HomeFeed
import com.shortdrama.ai.core.ui.mvi.UiEffect
import com.shortdrama.ai.core.ui.mvi.UiIntent
import com.shortdrama.ai.core.ui.mvi.UiState

/** Pill tabs under the Home header. Only [FOR_YOU] has content so far. */
enum class HomeTab { FOR_YOU, NEW, ORIGINAL, RANKING, CATEGORY }

sealed interface HomeIntent : UiIntent {
    data object Refresh : HomeIntent
    data class SelectTab(val tab: HomeTab) : HomeIntent
    data class BannerChanged(val position: Int) : HomeIntent
    data class OpenFilm(val filmId: String) : HomeIntent
}

data class HomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedTab: HomeTab = HomeTab.FOR_YOU,
    val feed: HomeFeed = HomeFeed.EMPTY,
    val bannerPosition: Int = 0,
) : UiState {
    val isEmpty: Boolean get() = !isLoading && feed.isEmpty
    val currentBanner: Film? get() = feed.banners.getOrNull(bannerPosition)
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToPlayer(val filmId: String) : HomeEffect
    data object ShowRefreshError : HomeEffect
}
