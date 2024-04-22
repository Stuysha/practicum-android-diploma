package ru.practicum.android.diploma.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FavoriteVacancyInteractor
import ru.practicum.android.diploma.ui.state.FavouritesScreenState

class FavouritesViewModel(private val interactor: FavoriteVacancyInteractor) : ViewModel() {
    private val _favoritesState = MutableLiveData<FavouritesScreenState>()
    val favoritesState: LiveData<FavouritesScreenState> = _favoritesState

    fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getListVacancy()
                .catch {
                    _favoritesState.postValue(FavouritesScreenState.Error)
                }
                .collect {
                    if (it.isEmpty()) {
                        _favoritesState.postValue(FavouritesScreenState.Empty)
                    } else {
                        _favoritesState.postValue(FavouritesScreenState.ShowContent(it))
                    }
                }
        }
    }

}
