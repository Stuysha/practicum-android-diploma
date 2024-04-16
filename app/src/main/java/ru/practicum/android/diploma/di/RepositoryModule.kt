package ru.practicum.android.diploma.di

import org.koin.dsl.bind
import org.koin.dsl.module
import ru.practicum.android.diploma.data.repository.FavoriteVacancyRepository
import ru.practicum.android.diploma.data.repository.FavoriteVacancyRepositoryImpl
import ru.practicum.android.diploma.data.repository.FiltersRepositoryImpl
import ru.practicum.android.diploma.data.repository.SearchRepositoryImpl
import ru.practicum.android.diploma.domain.api.FiltersRepository
import ru.practicum.android.diploma.domain.api.SearchRepository

val repositoryModule = module {
    factory { SearchRepositoryImpl(get(), get()) } bind SearchRepository::class
    factory { FavoriteVacancyRepositoryImpl(get()) } bind FavoriteVacancyRepository::class
    factory { FiltersRepositoryImpl(get(), get()) } bind FiltersRepository::class
}
