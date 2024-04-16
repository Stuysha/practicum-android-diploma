package ru.practicum.android.diploma.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.ui.state.RegionState

class RegionViewModel(private val filtersInteractor: FiltersInteractor) : ViewModel() {

    private val _state = MutableLiveData<RegionState>()
    val state: LiveData<RegionState> = _state
    private var regions: List<Area>? = null

    fun getRegions(areaId: String?) {
        _state.postValue(RegionState.Loading)

        viewModelScope.launch {
            processResult(filtersInteractor.getArea(areaId) as List<Area>)
        }
    }

    private fun processResult(data: List<Area>?) {
        if (data != null) {
            _state.postValue(RegionState.Content(data))
            regions = data
        } else {
            _state.postValue(RegionState.Error)
        }
    }

    fun getRegionsList() = regions
}
