package com.shortdrama.ai.core.domain.usecase

import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.model.HomeFeed
import javax.inject.Inject

/**
 * Splits the catalog into the Home sections. Pure business rule:
 * - banners: the first [BANNER_COUNT] films, in catalog order (the order published in Remote Config);
 * - big hits: the [BIG_HIT_COUNT] best rated films;
 * - recommended: every film.
 */
class BuildHomeFeedUseCase @Inject constructor() {
    operator fun invoke(films: List<Film>): HomeFeed = HomeFeed(
        banners = films.take(BANNER_COUNT),
        bigHits = films.sortedByDescending { it.rating ?: 0.0 }.take(BIG_HIT_COUNT),
        recommended = films,
    )

    companion object {
        const val BANNER_COUNT = 4
        const val BIG_HIT_COUNT = 4
    }
}
