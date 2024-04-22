package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.api.SearchInteractor
import ru.practicum.android.diploma.domain.impl.FavoriteVacancyImpl
import ru.practicum.android.diploma.domain.impl.FiltersInteractorImpl
import ru.practicum.android.diploma.domain.impl.SearchInteractorImpl
import ru.practicum.android.diploma.domain.interactors.FavoriteVacancyInteractor
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.sharing.SharingInteractor
import ru.practicum.android.diploma.domain.sharing.impl.SharingInteractorImpl

val interactorModule = module {

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }
    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }
    single<FavoriteVacancyInteractor> {
        FavoriteVacancyImpl(get())
    }
    single<FiltersInteractor> {
        FiltersInteractorImpl(get())
    }
}
