package ru.practicum.android.diploma.data.network.client

import android.content.Context
import retrofit2.HttpException
import ru.practicum.android.diploma.data.network.api.HeadHuntersApi
import ru.practicum.android.diploma.data.network.api.Request
import ru.practicum.android.diploma.data.network.api.Request.MainSearchRequest
import ru.practicum.android.diploma.data.network.api.Response
import ru.practicum.android.diploma.data.network.api.ResponseList
import ru.practicum.android.diploma.util.Constants.CODE_SUCCESS
import ru.practicum.android.diploma.util.Constants.NO_CONNECTION_ERROR
import ru.practicum.android.diploma.util.isConnected

class RetrofitNetworkClient(
    private val context: Context,
    private val headHuntersApi: HeadHuntersApi
) : NetworkClient {

    override suspend fun doRequest(request: Request): Response {
        if (!isConnected(context)) {
            return Response().apply { resultCode = NO_CONNECTION_ERROR }
        }
        var response = Response()
        return try {
            response = when (request) {
                is MainSearchRequest -> {
                    headHuntersApi.getVacancies(
                        query = request.query,
                        page = request.page,
                        filters = request.filters
                    )
                }

                is Request.SimilarVacanciesRequest -> {
                    headHuntersApi.getSimilarVacancies(request.id)
                }

                is Request.CurrentVacancyDetails -> {
                    headHuntersApi.getVacancy(request.id)
                }

                is Request.LoadAreas -> {
                    if (request.areaId.isNullOrEmpty()) {
                        ResponseList(data = headHuntersApi.loadFilterRegions())
                    } else {
                        headHuntersApi.loadFilterRegion(request.areaId)
                    }
                }

                Request.LoadIndustry -> {
                    ResponseList(headHuntersApi.loadFilterIndustry())
                }
            }
            response.apply { resultCode = CODE_SUCCESS }
        } catch (exception: HttpException) {
            response.apply { resultCode = exception.code() }
        }
    }
}
