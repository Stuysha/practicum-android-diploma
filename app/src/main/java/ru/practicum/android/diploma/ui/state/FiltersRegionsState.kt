package ru.practicum.android.diploma.ui.state

import ru.practicum.android.diploma.domain.models.filters.RegionDataItem

sealed interface FiltersRegionsState {
    object Loading : FiltersRegionsState
    data class Content(val regions: List<RegionDataItem>) : FiltersRegionsState
    object Error : FiltersRegionsState
    object Empty : FiltersRegionsState

    object Start : FiltersRegionsState
}
