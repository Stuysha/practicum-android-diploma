package ru.practicum.android.diploma.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.api.SearchInteractor
import ru.practicum.android.diploma.domain.models.SearchResponseModel
import ru.practicum.android.diploma.domain.models.VacancyModel
import ru.practicum.android.diploma.ui.state.SearchScreenState
import ru.practicum.android.diploma.util.Constants
import ru.practicum.android.diploma.util.ErrorVariant
import ru.practicum.android.diploma.util.adapter.SearchPage
import ru.practicum.android.diploma.util.adapter.ServerError
import ru.practicum.android.diploma.util.debounce
import java.net.ConnectException

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {
    private val _searchState = MutableLiveData<SearchScreenState>()
    val searchState: LiveData<SearchScreenState> = _searchState
    val actionStateFlow = MutableSharedFlow<String>()
    var isClickable = true
    private var found: Int? = null
    private var lastQuery: String? = null
    var stateRefresh: LoadState? = null
    var errorMessage = MutableLiveData<String?>()
    val stateVacancyData = actionStateFlow.flatMapLatest {
        getPagingData(it)
    }

    private val searchDebounce =
        debounce<String?>(Constants.SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { query ->
            viewModelScope.launch(Dispatchers.IO) {
                found = null
                if (query.isNullOrBlank() || query == lastQuery) return@launch
                lastQuery = query
                setState(SearchScreenState.Loading)
                actionStateFlow.emit(query)
            }
        }

    private val clickDebounce =
        debounce<Boolean>(Constants.SEARCH_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickable = it
        }

    fun onSearchQueryChange(query: String?) {
        searchDebounce(query?.trim())
    }

    private fun setState(state: SearchScreenState) {
        _searchState.postValue(state)
    }

    fun actionOnClick() {
        isClickable = false
        clickDebounce(true)
    }

    fun listener(loadState: CombinedLoadStates) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val refresh = loadState.source.refresh) {
                is LoadState.Error -> when (refresh.error) {
                    is ConnectException -> _searchState.value =
                        SearchScreenState.Error(ErrorVariant.NO_CONNECTION)
                    is NullPointerException -> _searchState.value =
                        SearchScreenState.Error(ErrorVariant.NO_CONTENT)
                    is ServerError -> _searchState.value =
                        SearchScreenState.Error(ErrorVariant.BAD_REQUEST)
                }

                LoadState.Loading -> {}
                is LoadState.NotLoading -> {
                    if (stateRefresh == LoadState.Loading) {
                        setState(SearchScreenState.Success(listOf(), found ?: 0))
                    }
                }
            }
            stateRefresh = loadState.source.refresh

            val errorState = when {
                loadState.source.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.source.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                else -> null
            }
            when (errorState?.error) {
                is ConnectException -> errorMessage.value = "Нет интернета"
            }
        }
    }

    suspend fun search(
        expression: String,
        page: Int,
    ): Response<out SearchResponseModel> {
        val result = searchInteractor.getVacancies(expression, page, HashMap())
        found = (result as? Response.Success<out SearchResponseModel>)?.data?.found ?: found
        return result
    }

    fun getPagingData(search: String): StateFlow<PagingData<VacancyModel>> {
        return Pager(PagingConfig(pageSize = Constants.VACANCIES_PER_PAGE)) {
            SearchPage(search, ::search)
        }.flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
    }

    fun clearMessageAddPlayList() {
        errorMessage.value = null
    }
}
