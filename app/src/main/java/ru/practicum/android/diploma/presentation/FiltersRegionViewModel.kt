package ru.practicum.android.diploma.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.RegionDataItem
import ru.practicum.android.diploma.ui.state.FiltersRegionsState
import java.text.RuleBasedCollator

class FiltersRegionViewModel(
    private val filterInteractor: FiltersInteractor,
) : ViewModel() {

    private val _filtersRegionStateLiveData = MutableLiveData<FiltersRegionsState>()
    val filtersRegionStateLiveData = _filtersRegionStateLiveData
    private val allAreas = mutableListOf<Area>()

    init {
        filtersRegionStateLiveData.postValue(FiltersRegionsState.Start)
    }

    fun init(country: String?) {
        if (allAreas.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                filtersRegionStateLiveData.postValue(FiltersRegionsState.Loading)
                loadRegions(country)
            }
        }
    }

    suspend fun loadRegions(idArea: String?) {
        when (val result = filterInteractor.getArea(idArea)) {
            is Response.Error -> {
                filtersRegionStateLiveData.postValue(FiltersRegionsState.Error)
            }

            is Response.Success -> {
                if (result.data.isEmpty()) {
                    filtersRegionStateLiveData.postValue(FiltersRegionsState.Error)
                } else {
                    allAreas.addAll(result.data)
                    filtersRegionStateLiveData.postValue(FiltersRegionsState.Start)
                    showAllArea()
                }
            }
        }
    }

    fun showAllArea() = viewModelScope.launch(Dispatchers.Default) {
        val content = mutableListOf<RegionDataItem>()
        allAreas.forEach {
            content.addAll(createAllItems(it, it))
        }
        content.sortBy { it.currentRegion.name }
        filtersRegionStateLiveData.postValue(FiltersRegionsState.Content(sortedArea(content)))
    }

    fun createAllItems(parentArea: Area, rootArea: Area): MutableList<RegionDataItem> {
        val content = mutableListOf<RegionDataItem>()
        parentArea.areas.forEach { area ->
            content.add(RegionDataItem(rootRegion = rootArea, currentRegion = area))
            content.addAll(createAllItems(area, rootArea))
        }
        return content
    }

    fun findArea(text: String) = viewModelScope.launch(Dispatchers.Default) {
        if (text.isEmpty()) {
            filtersRegionStateLiveData.postValue(FiltersRegionsState.Content(listOf()))
        } else {
            val content = mutableListOf<RegionDataItem>()
            allAreas.forEach {
                content.addAll(findChildArea(text.trim().uppercase(), it, it))
            }
            filtersRegionStateLiveData.postValue(FiltersRegionsState.Content(sortedArea(content)))
            if (content.isEmpty()) {
                filtersRegionStateLiveData.postValue(FiltersRegionsState.Empty)
            }
        }
    }

    fun findChildArea(text: String, parentArea: Area, rootArea: Area): MutableList<RegionDataItem> {
        val content = mutableListOf<RegionDataItem>()
        parentArea.areas.forEach { area ->
            if (area.name.trim().uppercase().indexOf(text) >= 0) {
                content.add(RegionDataItem(rootRegion = rootArea, currentRegion = area))
            }
            content.addAll(findChildArea(text, area, rootArea))
        }
        return content
    }

    fun sortedArea(list: List<RegionDataItem>): List<RegionDataItem> {
        val yoRule = "& а < б < в < г < д < е < ё < ж < з < и < й < к < л < м < н < о < п < р < с " +
            "< т < у < ф < х < ц < ч < ш < щ < ъ < ы < ь < я < ю < я"
        val ruleBasedCollator = RuleBasedCollator(yoRule)
        return list.sortedWith { a, b ->
            ruleBasedCollator.compare(a.currentRegion.name.lowercase(), b.currentRegion.name.lowercase())
        }
    }
}
