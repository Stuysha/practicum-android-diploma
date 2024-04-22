package ru.practicum.android.diploma.ui.state

import ru.practicum.android.diploma.domain.models.filters.Area

sealed interface FiltersCountriesState {
    object Loading : FiltersCountriesState
    data class Content(val countries: List<Area>) : FiltersCountriesState
    object Error : FiltersCountriesState
    object Empty : FiltersCountriesState

}
