package ru.practicum.android.diploma.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.CountrySortOrder
import ru.practicum.android.diploma.ui.state.FiltersCountriesState

class FiltersCountryViewModel(
    private val filterInteractor: FiltersInteractor,
) : ViewModel() {
    private var countries = ArrayList<Area>()
    private val filtersCountriesStateLiveData = MutableLiveData<FiltersCountriesState>()
    fun getFiltersCountriesStateLiveData(): LiveData<FiltersCountriesState> = filtersCountriesStateLiveData

    init {
        filtersCountriesStateLiveData.postValue(FiltersCountriesState.Loading)
    }

    fun fillData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = filterInteractor.getArea(null)
            loadCountries(result)
        }
    }

    private fun loadCountries(foundCountries: Response<out List<Area>>) {
        when (foundCountries) {
            is Response.Error -> filtersCountriesStateLiveData.postValue(FiltersCountriesState.Error)
            is Response.Success -> {
                if (foundCountries.data.isNotEmpty()) {
                    val countriesList = CountrySortOrder.sortCountriesListManually(foundCountries.data)
                    countries.clear()
                    countries.addAll(countriesList)
                    filtersCountriesStateLiveData.postValue(FiltersCountriesState.Content(countries))
                } else {
                    filtersCountriesStateLiveData.postValue(FiltersCountriesState.Empty)
                }
            }
        }
    }
}
