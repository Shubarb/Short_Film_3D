package com.shortdrama.ai.core.model

/** A single 3D AI short film that can be streamed from [videoUrl]. */
data class Film(
    val id: String,
    val title: String,
    val description: String,
    /** Landscape artwork (16:9). */
    val thumbnailUrl: String,
    val videoUrl: String,
    val durationSeconds: Long,
    val categoryId: String,
    /** Display genres, e.g. "Sci-Fi", "3D". */
    val tags: List<String>,
    val isPremium: Boolean,
    /** Portrait artwork (3:4) for banners and poster cards. Falls back to [thumbnailUrl]. */
    val posterUrl: String = thumbnailUrl,
    /** 0..10, null when the film has not been rated. */
    val rating: Double? = null,
    val releasedEpisodes: Int = 0,
    /** 0 when the film is not split into episodes. */
    val totalEpisodes: Int = 0,
    /** Corner badge such as "Top 10"; null hides it. */
    val badge: String? = null,
)
