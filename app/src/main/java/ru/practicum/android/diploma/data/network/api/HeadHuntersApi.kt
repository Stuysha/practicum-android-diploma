package ru.practicum.android.diploma.data.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.data.dto.SearchResponseDto
import ru.practicum.android.diploma.data.dto.VacancyDetailsDto
import ru.practicum.android.diploma.data.dto.fields.AreaDto
import ru.practicum.android.diploma.data.dto.fields.IndustryDto
import ru.practicum.android.diploma.util.Constants.VACANCIES_PER_PAGE

interface HeadHuntersApi {

    @GET("/vacancies/{vacancy_id}")
    suspend fun getVacancy(@Path("vacancy_id") id: String): VacancyDetailsDto

    @GET("/vacancies/{vacancy_id}/similar_vacancies")
    suspend fun getSimilarVacancies(@Path("vacancy_id") id: String): SearchResponseDto

    @GET("/vacancies")
    suspend fun getVacancies(
        @Query("text") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = VACANCIES_PER_PAGE,
        @QueryMap filters: Map<String, String>
    ): SearchResponseDto

    @GET("areas")
    suspend fun loadFilterRegions(): List<AreaDto>

    @GET("areas/{area_id}")
    suspend fun loadFilterRegion(@Path("area_id") areaId: String): AreaDto

    @GET("industries")
    suspend fun loadFilterIndustry(): List<IndustryDto>
}
