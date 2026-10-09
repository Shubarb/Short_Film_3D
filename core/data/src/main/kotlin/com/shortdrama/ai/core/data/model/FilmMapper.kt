package com.shortdrama.ai.core.data.model

import com.shortdrama.ai.core.model.Category
import com.shortdrama.ai.core.model.Film
import com.shortdrama.ai.core.model.FilmCatalog

fun FilmCatalogDto.toDomain(): FilmCatalog = FilmCatalog(
    version = version,
    categories = categories
        .filter { it.id.isNotBlank() }
        .map { Category(id = it.id, title = it.title.ifBlank { it.id }) },
    films = films
        // A film without an id or a playable link is useless to the app; drop it.
        .filter { it.id.isNotBlank() && it.videoUrl.isNotBlank() }
        .distinctBy { it.id }
        .map { it.toDomain() },
)

fun FilmDto.toDomain(): Film = Film(
    id = id,
    title = title,
    description = description,
    thumbnailUrl = thumbnailUrl,
    videoUrl = videoUrl,
    durationSeconds = durationSec,
    categoryId = categoryId,
    tags = tags,
    isPremium = isPremium,
    posterUrl = posterUrl.ifBlank { thumbnailUrl },
    rating = rating?.takeIf { it > 0 }?.coerceAtMost(10.0),
    releasedEpisodes = releasedEpisodes.coerceIn(0, totalEpisodes.coerceAtLeast(0)),
    totalEpisodes = totalEpisodes.coerceAtLeast(0),
    badge = badge.takeIf { it.isNotBlank() },
)
