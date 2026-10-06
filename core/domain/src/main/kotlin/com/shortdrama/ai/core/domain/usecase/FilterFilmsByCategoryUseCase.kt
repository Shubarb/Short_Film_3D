package com.shortdrama.ai.core.domain.usecase

import com.shortdrama.ai.core.model.Film
import javax.inject.Inject

/** Pure business rule: a null category means "All". */
class FilterFilmsByCategoryUseCase @Inject constructor() {
    operator fun invoke(films: List<Film>, categoryId: String?): List<Film> =
        if (categoryId == null) films else films.filter { it.categoryId == categoryId }
}
