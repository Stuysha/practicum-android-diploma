package ru.practicum.android.diploma.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.practicum.android.diploma.domain.interactors.FavoriteVacancyInteractor
import ru.practicum.android.diploma.util.Constants
import ru.practicum.android.diploma.util.debounce

class FavouritesViewModel(private val interactor: FavoriteVacancyInteractor) : ViewModel() {

    var isClickable = true
    val state = interactor.getListVacancy()

    private val clickDebounce =
        debounce<Boolean>(Constants.SEARCH_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickable = it
        }

    fun actionOnClick() {
        isClickable = false
        clickDebounce(true)
    }
}
