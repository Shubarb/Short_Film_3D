package com.shortdrama.ai.core.model

/** A single 3D AI short film that can be streamed from [videoUrl]. */
data class Film(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val durationSeconds: Long,
    val categoryId: String,
    val tags: List<String>,
    val isPremium: Boolean,
)
