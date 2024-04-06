package ru.practicum.android.diploma.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.models.SearchResponseModel
import ru.practicum.android.diploma.domain.models.VacancyModel

interface SearchInteractor {
    suspend fun getSimilarVacancies(id: String): Response<out SearchResponseModel>

    suspend fun getCurrentVacancyDetails(id: String): Response<out VacancyModel>

    suspend fun getVacancies(
        query: String,
        page: Int,
        filters: HashMap<String, String>
    ): Response<out SearchResponseModel>
}
