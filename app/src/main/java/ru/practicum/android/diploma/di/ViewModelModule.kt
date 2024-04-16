package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.FavouritesViewModel
import ru.practicum.android.diploma.presentation.FilterViewModel
import ru.practicum.android.diploma.presentation.RegionViewModel
import ru.practicum.android.diploma.presentation.SearchViewModel
import ru.practicum.android.diploma.presentation.VacancyViewModel

val viewModelModule = module {
    viewModel { SearchViewModel(get(), get()) }
    viewModel { FavouritesViewModel(get()) }
    viewModel { VacancyViewModel(get(), get(), get()) }
    viewModel { FilterViewModel(get()) }
    viewModel { RegionViewModel(get()) }
}
