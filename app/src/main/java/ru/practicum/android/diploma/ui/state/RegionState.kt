package ru.practicum.android.diploma.ui.state

import ru.practicum.android.diploma.domain.models.filters.Area

sealed interface RegionState {

    object Loading : RegionState
    data object Error : RegionState
    data class Content(val data: List<Area>) : RegionState
}
