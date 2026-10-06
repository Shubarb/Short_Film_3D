package com.shortdrama.ai.core.domain.usecase

import com.shortdrama.ai.core.domain.repository.FilmRepository
import javax.inject.Inject

class RefreshFilmCatalogUseCase @Inject constructor(
    private val repository: FilmRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.refresh()
}
