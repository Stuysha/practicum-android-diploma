package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.FavouritesViewModel
import ru.practicum.android.diploma.presentation.FilterViewModel
import ru.practicum.android.diploma.presentation.FiltersCountryViewModel
import ru.practicum.android.diploma.presentation.FiltersRegionViewModel
import ru.practicum.android.diploma.presentation.IndustryFilterViewModel
import ru.practicum.android.diploma.presentation.SearchViewModel
import ru.practicum.android.diploma.presentation.VacancyViewModel

val viewModelModule = module {
    viewModel { SearchViewModel(get(), get()) }
    viewModel { FavouritesViewModel(get()) }
    viewModel { VacancyViewModel(get(), get(), get()) }
    viewModel { FilterViewModel(get()) }
    viewModel { IndustryFilterViewModel(get()) }
    viewModel { FiltersCountryViewModel(get()) }
    viewModel { FiltersRegionViewModel(get()) }
}
