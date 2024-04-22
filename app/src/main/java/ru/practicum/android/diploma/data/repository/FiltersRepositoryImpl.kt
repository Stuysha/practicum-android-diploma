package ru.practicum.android.diploma.data.repository

import ru.practicum.android.diploma.data.dto.fields.AreaDto
import ru.practicum.android.diploma.data.dto.fields.IndustryDto
import ru.practicum.android.diploma.data.mapper.convertorToArea
import ru.practicum.android.diploma.data.mapper.convertorToIndustry
import ru.practicum.android.diploma.data.network.api.Request
import ru.practicum.android.diploma.data.network.api.ResponseList
import ru.practicum.android.diploma.data.network.client.NetworkClient
import ru.practicum.android.diploma.data.storage.FiltersLocalStorage
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.api.FiltersRepository
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings
import ru.practicum.android.diploma.domain.models.filters.Industry
import ru.practicum.android.diploma.util.Constants

class FiltersRepositoryImpl(
    private val networkClient: NetworkClient,
    private val filtersLocalStorage: FiltersLocalStorage,
) : FiltersRepository {

    override fun getPrefs(): FiltersSettings = filtersLocalStorage.getPrefs()

    override fun savePrefs(settings: FiltersSettings) {
        filtersLocalStorage.savePrefs(settings)
    }

    override fun clearPrefs() {
        filtersLocalStorage.clearPrefs()
    }

    override suspend fun getArea(id: String?): Response<out List<Area>> {
        val response = networkClient.doRequest(Request.LoadAreas(id))
        return if (response.resultCode == Constants.CODE_SUCCESS) {
            when (response) {
                is AreaDto -> {
                    Response.Success(listOf(response.convertorToArea()))
                }

                is ResponseList<*> -> {
                    val ares = (response.data as? List<AreaDto>)?.map { it.convertorToArea() } ?: listOf()
                    Response.Success(ares)
                }

                else -> Response.Error(getErrorType(response.resultCode))
            }
        } else {
            Response.Error(getErrorType(response.resultCode))
        }
    }

    override suspend fun getIndustry(): Response<out List<Industry>> {
        val response = networkClient.doRequest(Request.LoadIndustry)
        return if (response.resultCode == Constants.CODE_SUCCESS) {
            val result =
                (response as ResponseList<List<IndustryDto>>).data?.map { it.convertorToIndustry() } ?: listOf()
            Response.Success(result)
        } else {
            Response.Error(getErrorType(response.resultCode))
        }
    }
}
