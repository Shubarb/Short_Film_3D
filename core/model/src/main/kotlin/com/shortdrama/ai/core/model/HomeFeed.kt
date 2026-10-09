package com.shortdrama.ai.core.model

/** Sections of the Home "For You" tab. */
data class HomeFeed(
    val banners: List<Film>,
    val bigHits: List<Film>,
    val recommended: List<Film>,
) {
    val isEmpty: Boolean get() = banners.isEmpty() && bigHits.isEmpty() && recommended.isEmpty()

    companion object {
        val EMPTY = HomeFeed(banners = emptyList(), bigHits = emptyList(), recommended = emptyList())
    }
}
