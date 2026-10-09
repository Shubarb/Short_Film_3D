package com.shortdrama.ai.core.domain.usecase

import com.shortdrama.ai.core.model.Film
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildHomeFeedUseCaseTest {

    private val buildHomeFeed = BuildHomeFeedUseCase()

    @Test
    fun `banners keep catalog order, big hits are the best rated`() {
        val films = listOf(
            film("a", rating = 7.0),
            film("b", rating = null),
            film("c", rating = 9.5),
            film("d", rating = 8.0),
            film("e", rating = 9.0),
        )

        val feed = buildHomeFeed(films)

        assertEquals(listOf("a", "b", "c", "d"), feed.banners.map { it.id })
        assertEquals(listOf("c", "e", "d", "a"), feed.bigHits.map { it.id })
        assertEquals(films, feed.recommended)
    }

    @Test
    fun `empty catalog gives an empty feed`() {
        assertTrue(buildHomeFeed(emptyList()).isEmpty)
    }

    private fun film(id: String, rating: Double?) = Film(
        id = id,
        title = id,
        description = "",
        thumbnailUrl = "",
        videoUrl = "https://x/$id.mp4",
        durationSeconds = 0,
        categoryId = "",
        tags = emptyList(),
        isPremium = false,
        rating = rating,
    )
}
