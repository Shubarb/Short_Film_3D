package com.shortdrama.ai.core.data.parser

import com.shortdrama.ai.core.model.FilmCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FilmCatalogParserTest {

    private val parser = FilmCatalogParser()

    @Test
    fun `parses films and ignores unknown keys`() {
        val raw = """
            {
              "version": 2,
              "unknownField": true,
              "categories": [{ "id": "scifi", "title": "Sci-Fi" }],
              "films": [
                { "id": "a", "title": "A", "videoUrl": "https://x/a.mp4", "categoryId": "scifi", "durationSec": 60 }
              ]
            }
        """.trimIndent()

        val catalog = parser.parse(raw)

        assertEquals(2, catalog.version)
        assertEquals(1, catalog.films.size)
        assertEquals("https://x/a.mp4", catalog.films.first().videoUrl)
        assertEquals(60L, catalog.films.first().durationSeconds)
    }

    @Test
    fun `drops films without id or video url and duplicates`() {
        val raw = """
            { "films": [
                { "id": "a", "videoUrl": "https://x/a.mp4" },
                { "id": "a", "videoUrl": "https://x/a2.mp4" },
                { "id": "", "videoUrl": "https://x/b.mp4" },
                { "id": "c", "videoUrl": "" }
            ] }
        """.trimIndent()

        val catalog = parser.parse(raw)

        assertEquals(listOf("a"), catalog.films.map { it.id })
    }

    @Test
    fun `blank input yields empty catalog`() {
        assertEquals(FilmCatalog.EMPTY, parser.parse("   "))
    }

    @Test
    fun `malformed json throws`() {
        assertThrows(Exception::class.java) { parser.parse("{ not json") }
    }
}
