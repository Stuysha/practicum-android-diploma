package ru.practicum.android.diploma.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.filters.Industry
import ru.practicum.android.diploma.domain.models.filters.SubIndustry
import ru.practicum.android.diploma.ui.state.IndustryFilterState
import ru.practicum.android.diploma.util.adapter.industry.IndustryAdapterItem

class IndustryFilterViewModel(private val filtersInteractor: FiltersInteractor) : ViewModel() {
    private val _industryState = MutableLiveData<IndustryFilterState>()
    val industryState: LiveData<IndustryFilterState> = _industryState
    private var currentIndustriesList = ArrayList<SubIndustry>()

    fun getIndustries() {
        _industryState.postValue(IndustryFilterState.Load)
        viewModelScope.launch {
            val response = filtersInteractor.getIndustry()
            loadIndustryList(response)
        }
    }

    private fun loadIndustryList(response: Response<out List<Industry>>) {
        when (response) {
            is Response.Success -> {
                val industries = response.data
                currentIndustriesList.clear()
                currentIndustriesList.addAll(sortIndustryList(industries))
                _industryState.postValue(
                    IndustryFilterState.Success(currentIndustriesList.map { IndustryAdapterItem(it) })
                )
            }

            is Response.Error -> {
                _industryState.value = IndustryFilterState.Error
            }
        }
    }

    private fun sortIndustryList(industriesList: List<Industry>): List<SubIndustry> {
        val sortedSubIndustryList: MutableList<SubIndustry> = mutableListOf()
        for (industry in industriesList) {
            for (subindustry in industry.industries) {
                sortedSubIndustryList.add(
                    SubIndustry(
                        id = subindustry.id,
                        name = subindustry.name
                    )
                )
            }
            sortedSubIndustryList.add(
                SubIndustry(
                    id = industry.id,
                    name = industry.name
                )
            )
        }
        return sortedSubIndustryList.sortedBy { it.name }
    }

    fun filterIndustry(query: String) {
        if (query.isNotEmpty()) {
            val filteredList = currentIndustriesList.filter {
                it.name.contains(query, ignoreCase = true)
            }
            if (filteredList.isNotEmpty()) {
                _industryState.value = IndustryFilterState.Success(filteredList.map { IndustryAdapterItem(it) })
            } else {
                _industryState.value = IndustryFilterState.Empty
            }
        } else {
            _industryState.value =
                IndustryFilterState.Success(currentIndustriesList.map { IndustryAdapterItem(it) })
        }
    }
}
