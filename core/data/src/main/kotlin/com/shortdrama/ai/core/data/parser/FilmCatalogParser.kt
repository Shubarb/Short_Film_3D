package com.shortdrama.ai.core.data.parser

import com.shortdrama.ai.core.data.model.FilmCatalogDto
import com.shortdrama.ai.core.data.model.toDomain
import com.shortdrama.ai.core.model.FilmCatalog
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FilmCatalogParser @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /** Returns [FilmCatalog.EMPTY] for blank input; throws on malformed JSON so callers can log it. */
    fun parse(raw: String): FilmCatalog {
        if (raw.isBlank()) return FilmCatalog.EMPTY
        return json.decodeFromString<FilmCatalogDto>(raw).toDomain()
    }
}
