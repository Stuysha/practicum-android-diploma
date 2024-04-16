package ru.practicum.android.diploma.ui.state

import ru.practicum.android.diploma.util.adapter.industry.IndustryAdapterItem

sealed interface IndustryFilterState {
    data object Load : IndustryFilterState
    data object Error : IndustryFilterState
    data object Empty : IndustryFilterState
    data class Success(val data: List<IndustryAdapterItem>) : IndustryFilterState
}
