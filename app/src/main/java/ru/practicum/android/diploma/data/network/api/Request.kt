package ru.practicum.android.diploma.data.network.api

sealed interface Request {
    data class MainSearchRequest(
        val query: String,
        val page: Int,
        val filters: Map<String, String>
    ) : Request

    data class SimilarVacanciesRequest(val id: String) : Request

    data class CurrentVacancyDetails(val id: String) : Request

    data class LoadAreas(val areaId: String?) : Request

    data object LoadIndustry : Request
}
