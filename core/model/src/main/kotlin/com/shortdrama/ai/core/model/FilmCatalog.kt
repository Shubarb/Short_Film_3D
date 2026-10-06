package com.shortdrama.ai.core.model

/** Everything the app knows about available films, as published through Remote Config. */
data class FilmCatalog(
    val version: Int,
    val categories: List<Category>,
    val films: List<Film>,
) {
    companion object {
        val EMPTY = FilmCatalog(version = 0, categories = emptyList(), films = emptyList())
    }
}
