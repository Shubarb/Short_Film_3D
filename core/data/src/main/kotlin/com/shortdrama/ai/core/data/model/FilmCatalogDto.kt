package com.shortdrama.ai.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire format of the `film_catalog` Remote Config parameter. Every field has a default so
 * that a partially filled JSON published from the Firebase console still parses.
 */
@Serializable
data class FilmCatalogDto(
    @SerialName("version") val version: Int = 0,
    @SerialName("categories") val categories: List<CategoryDto> = emptyList(),
    @SerialName("films") val films: List<FilmDto> = emptyList(),
)

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String = "",
    @SerialName("title") val title: String = "",
)

@Serializable
data class FilmDto(
    @SerialName("id") val id: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("thumbnailUrl") val thumbnailUrl: String = "",
    @SerialName("videoUrl") val videoUrl: String = "",
    @SerialName("durationSec") val durationSec: Long = 0,
    @SerialName("categoryId") val categoryId: String = "",
    @SerialName("tags") val tags: List<String> = emptyList(),
    @SerialName("isPremium") val isPremium: Boolean = false,
    @SerialName("posterUrl") val posterUrl: String = "",
    @SerialName("rating") val rating: Double? = null,
    @SerialName("releasedEpisodes") val releasedEpisodes: Int = 0,
    @SerialName("totalEpisodes") val totalEpisodes: Int = 0,
    @SerialName("badge") val badge: String = "",
)
