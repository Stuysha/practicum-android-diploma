package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.api.SearchInteractor
import ru.practicum.android.diploma.domain.api.SearchRepository
import ru.practicum.android.diploma.domain.models.SearchResponseModel
import ru.practicum.android.diploma.domain.models.VacancyModel

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {
    override suspend fun getSimilarVacancies(id: String): Response<out SearchResponseModel> {
        return searchRepository.getSimilarVacancies(id)
    }

    override suspend fun getCurrentVacancyDetails(id: String): Response<out VacancyModel> {
        return searchRepository.getCurrentVacancyDetails(id)
    }

    override suspend fun getVacancies(
        query: String,
        page: Int,
        filters: HashMap<String, String>
    ): Response<out SearchResponseModel> {
        return searchRepository.getVacancies(query, page, filters)
    }
}
