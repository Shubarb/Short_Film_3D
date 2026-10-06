package com.shortdrama.ai.feature.home

import androidx.lifecycle.viewModelScope
import com.shortdrama.ai.core.domain.usecase.FilterFilmsByCategoryUseCase
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
    private val filterFilmsByCategory: FilterFilmsByCategoryUseCase,
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    init {
        observeFilmCatalog()
            .onEach { catalog ->
                setState {
                    // Keep the selected tab only if it still exists in the new catalog.
                    val selected = selectedCategoryId
                        ?.takeIf { id -> catalog.categories.any { it.id == id } }
                    copy(
                        isLoading = false,
                        categories = catalog.categories,
                        selectedCategoryId = selected,
                        allFilms = catalog.films,
                        films = filterFilmsByCategory(catalog.films, selected),
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
            is HomeIntent.SelectCategory -> setState {
                copy(
                    selectedCategoryId = intent.categoryId,
                    films = filterFilmsByCategory(allFilms, intent.categoryId),
                )
            }
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
