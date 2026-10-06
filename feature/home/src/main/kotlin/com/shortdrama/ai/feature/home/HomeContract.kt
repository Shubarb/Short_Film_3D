package com.shortdrama.ai.feature.home

import com.shortdrama.ai.core.model.Category
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.ui.mvi.UiEffect
import com.shortdrama.ai.core.ui.mvi.UiIntent
import com.shortdrama.ai.core.ui.mvi.UiState

sealed interface HomeIntent : UiIntent {
    data object Refresh : HomeIntent
    data class SelectCategory(val categoryId: String?) : HomeIntent
    data class OpenFilm(val filmId: String) : HomeIntent
}

data class HomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val categories: List<Category> = emptyList(),
    /** null = "All". */
    val selectedCategoryId: String? = null,
    val allFilms: List<Film> = emptyList(),
    val films: List<Film> = emptyList(),
) : UiState {
    val isEmpty: Boolean get() = !isLoading && films.isEmpty()
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToPlayer(val filmId: String) : HomeEffect
    data object ShowRefreshError : HomeEffect
}
