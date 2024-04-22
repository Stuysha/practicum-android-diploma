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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.api.SearchInteractor
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.MessageData
import ru.practicum.android.diploma.domain.models.SearchResponseModel
import ru.practicum.android.diploma.domain.models.VacancyModel
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings
import ru.practicum.android.diploma.domain.models.filters.checkEmpty
import ru.practicum.android.diploma.ui.state.SearchScreenState
import ru.practicum.android.diploma.util.Constants
import ru.practicum.android.diploma.util.ErrorVariant
import ru.practicum.android.diploma.util.adapter.SearchPage
import ru.practicum.android.diploma.util.adapter.ServerError
import ru.practicum.android.diploma.util.debounce
import java.net.ConnectException

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val filterInteractor: FiltersInteractor
) : ViewModel() {
    private val sizeLoadPage = 1
    private var jobFilter: Job? = null
    private val _searchState = MutableLiveData<SearchScreenState>()
    val searchState: LiveData<SearchScreenState> = _searchState
    private val actionStateFlow = MutableStateFlow<String?>(null)
    var isClickable = true
    private var found: Int? = null
    var lastQuery: String? = null
    private var stateRefresh: LoadState? = null
    private var _errorMessage = MutableLiveData<MessageData?>()
    var errorMessage: LiveData<MessageData?> = _errorMessage

    val stateVacancyData = actionStateFlow.filter { it != null }.flatMapMerge {
        setState(SearchScreenState.Loading)
        getPagingData(it!!)
    }

    private var filtersSetting: FiltersSettings? = null
    val stateFilters = MutableStateFlow(false)

    init {
        jobFilter = viewModelScope.launch(Dispatchers.IO) {
            filtersSetting = filterInteractor.getPrefs()
            stateFilters.value = filtersSetting?.checkEmpty() ?: false
        }
    }

    fun checkChangeFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            if (jobFilter?.isActive != true) {
                val newFilters = filterInteractor.getPrefs()
                if (newFilters != filtersSetting) {
                    filtersSetting = newFilters
                    stateFilters.value = filtersSetting?.checkEmpty() ?: false
                    if (lastQuery?.isNotEmpty() == true) {
                        actionStateFlow.emit(null)
                        actionStateFlow.emit(lastQuery!!)
                    }
                }
            }
        }
    }

    private val searchDebounce =
        debounce<String?>(Constants.SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { query ->
            viewModelScope.launch(Dispatchers.IO) {
                val state = _searchState.value
                if (query?.isNotEmpty() == true && (query != lastQuery || state is SearchScreenState.Error)) {
                    found = null
                    lastQuery = query
                    actionStateFlow.emit(query)
                } else if (query?.trim() == lastQuery?.trim() && query?.isNotEmpty() == true) {
                    if (state != null) {
                        setState(SearchScreenState.Default)
                        _searchState.postValue(state)
                    }
                }
                if (query.isNullOrEmpty()) {
                    lastQuery = null
                    actionStateFlow.emit(null)
                    setState(SearchScreenState.Default)
                }
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
                is LoadState.Error -> {
                    actionStateFlow.emit(null)
                    when (refresh.error) {
                        is ConnectException -> _searchState.value =
                            SearchScreenState.Error(ErrorVariant.NO_CONNECTION)

                        is NullPointerException -> _searchState.value =
                            SearchScreenState.Error(ErrorVariant.NO_CONTENT)

                        is ServerError -> _searchState.value =
                            SearchScreenState.Error(ErrorVariant.BAD_REQUEST)
                    }
                }

                LoadState.Loading -> {
                }

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
                is ConnectException -> _errorMessage.value = MessageData(R.string.no_connection)
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
        return Pager(PagingConfig(pageSize = Constants.VACANCIES_PER_PAGE, initialLoadSize = sizeLoadPage)) {
            SearchPage(search, ::search)
        }.flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
    }

    fun clearMessage() {
        _errorMessage.value = null
    }
}
