package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.FavouritesViewModel
import ru.practicum.android.diploma.presentation.SearchViewModel
import ru.practicum.android.diploma.presentation.VacancyViewModel

val viewModelModule = module {
    viewModel { SearchViewModel(get(), androidContext()) }
    viewModel { FavouritesViewModel(get()) }
    viewModel { VacancyViewModel(get(), get(), get()) }
}
