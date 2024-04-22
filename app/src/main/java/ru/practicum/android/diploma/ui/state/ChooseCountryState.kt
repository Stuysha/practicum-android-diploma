package ru.practicum.android.diploma.ui.state

import ru.practicum.android.diploma.domain.models.fields.AreaModel

interface ChooseCountryState {

    data class Success(val areas: List<AreaModel>) : ChooseCountryState

    data class Error(val message: String = "") : ChooseCountryState

    object Initial : ChooseCountryState
}
