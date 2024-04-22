package ru.practicum.android.diploma.data.repository

import ru.practicum.android.diploma.data.dto.SearchResponseDto
import ru.practicum.android.diploma.data.dto.VacancyDetailsDto
import ru.practicum.android.diploma.data.mapper.mapToModel
import ru.practicum.android.diploma.data.network.api.Request
import ru.practicum.android.diploma.data.network.client.NetworkClient
import ru.practicum.android.diploma.data.storage.FiltersLocalStorage
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.api.SearchRepository
import ru.practicum.android.diploma.domain.models.SearchResponseModel
import ru.practicum.android.diploma.domain.models.VacancyDetailsModel
import ru.practicum.android.diploma.util.Constants
import ru.practicum.android.diploma.util.Constants.BAD_REQUEST_ERROR
import ru.practicum.android.diploma.util.Constants.NO_CONNECTION_ERROR
import ru.practicum.android.diploma.util.ErrorVariant

class SearchRepositoryImpl(
    private val filtersLocalStorage: FiltersLocalStorage,
    private val networkClient: NetworkClient
) : SearchRepository {

    override suspend fun getSimilarVacancies(id: String): Response<out SearchResponseModel> {
        val response = networkClient.doRequest(Request.SimilarVacanciesRequest(id))
        return if (response.resultCode == Constants.CODE_SUCCESS) {
            Response.Success((response as SearchResponseDto).mapToModel())
        } else {
            Response.Error(getErrorType(response.resultCode))
        }
    }

    override suspend fun getCurrentVacancyDetails(id: String): Response<out VacancyDetailsModel> {
        val response = networkClient.doRequest(Request.CurrentVacancyDetails(id))
        return if (response.resultCode == Constants.CODE_SUCCESS) {
            Response.Success((response as VacancyDetailsDto).mapToModel())
        } else {
            Response.Error(getErrorType(response.resultCode))
        }
    }

    override suspend fun getVacancies(
        query: String,
        page: Int,
        filters: HashMap<String, String>
    ): Response<out SearchResponseModel> {
        val prefs = filtersLocalStorage.getPrefs()

        val countryId = prefs.countryId
        val regionId = prefs.regionId
        val industryId = prefs.industryId
        val salary = prefs.expectedSalary
        val salaryOnly = prefs.salaryOnlyCheckbox

        val options = mutableMapOf<String, String>()

        if (countryId.isNotEmpty()) {
            if (regionId.isNotEmpty()) {
                options[Constants.AREA] = regionId
            } else {
                options[Constants.AREA] = countryId
            }
        }

        if (industryId.isNotEmpty()) {
            options[Constants.INDUSTRY] = industryId
        }

        if (salary.isNotEmpty()) {
            options[Constants.SALARY] = salary
        }

        if (salaryOnly) {
            options[Constants.ONLY_WITH_SALARY] = salaryOnly.toString()
        }

        val response = try {
            networkClient.doRequest(Request.MainSearchRequest(query, page, options.toMap()))
        } catch (_: Throwable) {
            return Response.Error(ErrorVariant.BAD_REQUEST)
        }
        return if (response.resultCode == Constants.CODE_SUCCESS) {
            Response.Success((response as SearchResponseDto).mapToModel())
        } else {
            Response.Error(getErrorType(response.resultCode))
        }
    }
}

fun getErrorType(code: Int): ErrorVariant = when {
    code == NO_CONNECTION_ERROR -> ErrorVariant.NO_CONNECTION
    code >= BAD_REQUEST_ERROR -> ErrorVariant.BAD_REQUEST
    else -> ErrorVariant.BAD_REQUEST
}
