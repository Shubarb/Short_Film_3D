package com.shortdrama.ai.core.domain.usecase

import com.shortdrama.ai.core.domain.repository.FilmRepository
import com.shortdrama.ai.core.model.Film
import javax.inject.Inject

class GetFilmUseCase @Inject constructor(
    private val repository: FilmRepository,
) {
    suspend operator fun invoke(id: String): Film? = repository.getFilm(id)
}
