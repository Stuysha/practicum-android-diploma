package ru.practicum.android.diploma.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings
import ru.practicum.android.diploma.domain.models.filters.SubIndustry
import ru.practicum.android.diploma.domain.models.filters.checkEmpty
import ru.practicum.android.diploma.ui.state.FiltersState
import kotlin.properties.Delegates

class FilterViewModel(private val filtersInteractor: FiltersInteractor) : ViewModel() {
    private val _filterState = MutableStateFlow(FiltersState())
    val filterState: StateFlow<FiltersState> = _filterState.asStateFlow()
    private var _filter: FiltersSettings? = null
    private var _oldFilter: FiltersSettings? = null

    private var filter: FiltersSettings? by Delegates.observable(_filter) { _, _, newValue ->
        if (newValue != null) {
            monitorChangesFilter()
        }
    }

    private var oldFilter: FiltersSettings? by Delegates.observable(_oldFilter) { _, _, _ ->
    }

    private fun monitorChangesFilter() {
        viewModelScope.launch {
            val showApply = filter != oldFilter
            withContext(Dispatchers.Main) {
                _filterState.value = filterState.value.copy(
                    filters = filter!!,
                    showApply = showApply,
                    showClear = showApply || filter?.checkEmpty() == false
                )
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            oldFilter = getPrefs()
            filter = oldFilter?.copy()
        }
    }

    fun setNewRegionCountry(country: String?, countryId: String?, regionId: String?, region: String?) {
        filter = filter?.copy(
            country = country ?: "",
            countryId = countryId ?: "",
            region = region ?: "",
            regionId = regionId ?: ""
        )
    }

    fun setNewIndustry(industry: SubIndustry?) {
        filter = filter?.copy(
            industry = industry?.name ?: "",
            industryId = industry?.id ?: ""
        )
    }

    fun setNewCounterAndRegion(country: Area?, region: Area?) {
        filter = filter?.copy(
            country = country?.name ?: "",
            countryId = country?.id ?: "",
            region = region?.name ?: "",
            regionId = region?.id ?: ""
        )
    }

    fun setExpectedSalary(expectedSalary: String?) {
        filter = filter?.copy(
            expectedSalary = expectedSalary ?: ""
        )
    }

    fun setSalaryOnlyCheckbox(salaryOnlyCheckbox: Boolean) {
        filter = filter?.copy(
            salaryOnlyCheckbox = salaryOnlyCheckbox
        )
    }

    private fun getPrefs(): FiltersSettings = filtersInteractor.getPrefs()

    fun savePrefs() {
        filter?.let {
            filtersInteractor.savePrefs(it)
        }
    }

    fun clearPrefs() {
        viewModelScope.launch(Dispatchers.IO) {
            filtersInteractor.clearPrefs()
            oldFilter = getPrefs()
            filter = oldFilter?.copy()
        }
    }

    fun getIndustryId(): String? {
        return filter?.industryId
    }

    fun getActualCountryAndRegion(): Pair<Area?, Area?> {
        return if (filter?.country?.isNotEmpty() == true) {
            val country = Area(id = filter!!.countryId, name = filter!!.country, parentId = "", areas = listOf())
            val area = if (filter?.region?.isNotEmpty() == true) {
                Area(id = filter!!.regionId, name = filter!!.region, parentId = "", areas = listOf())
            } else {
                null
            }
            country to area
        } else {
            null to null
        }
    }
}
