package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.Response
import ru.practicum.android.diploma.domain.api.FiltersRepository
import ru.practicum.android.diploma.domain.interactors.FiltersInteractor
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings
import ru.practicum.android.diploma.domain.models.filters.Industry

class FiltersInteractorImpl(
    private val filtersRepository: FiltersRepository
) : FiltersInteractor {

    override fun getPrefs(): FiltersSettings = filtersRepository.getPrefs()

    override fun savePrefs(settings: FiltersSettings) {
        filtersRepository.savePrefs(settings)
    }

    override fun clearPrefs() {
        filtersRepository.clearPrefs()
    }

    override suspend fun getArea(id: String?): Response<out List<Area>> = filtersRepository.getArea(id)

    override suspend fun getIndustry(): Response<out List<Industry>> = filtersRepository.getIndustry()
}
