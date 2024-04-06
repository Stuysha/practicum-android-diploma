package ru.practicum.android.diploma.util.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.models.SearchResponseModel
import ru.practicum.android.diploma.domain.models.VacancyModel
import ru.practicum.android.diploma.util.Constants.VACANCIES_PER_PAGE
import ru.practicum.android.diploma.util.ErrorVariant.BAD_REQUEST
import ru.practicum.android.diploma.util.ErrorVariant.NO_CONNECTION
import java.net.ConnectException

open class SearchPage(
    private val query: String,
    private val search: suspend (String, Int) -> Response<out SearchResponseModel>,
) : PagingSource<Int, VacancyModel>() {

    override fun getRefreshKey(state: PagingState<Int, VacancyModel>): Int? {
        val anchorPosition = state.anchorPosition
        if (anchorPosition != null) {
            val page = state.closestPageToPosition(anchorPosition)
            if (page != null) {
                return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VacancyModel> {
        if (query.isEmpty()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }
        val page: Int = params.key ?: 0
        val pageSize: Int = VACANCIES_PER_PAGE
        return when (val response = search(query, page)) {
            is Response.Error -> {
                when (response.error) {
                    NO_CONNECTION -> LoadResult.Error(ConnectException())
                    BAD_REQUEST -> LoadResult.Error(ServerError())
                    else -> LoadResult.Error(NullPointerException())
                }
            }

            is Response.Success -> {
                if (response.data.vacancies.isNotEmpty()) {
                    val data = response.data.vacancies
                    val nextKey = if (data.size < pageSize) null else page + 1
                    val prevKey = if (page == 0) null else page - 1
                    LoadResult.Page(data, prevKey, nextKey)
                } else {
                    LoadResult.Error(NullPointerException())
                }
            }
        }
    }
}

class ServerError : Exception()
