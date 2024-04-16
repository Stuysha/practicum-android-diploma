package ru.practicum.android.diploma.ui.state

import ru.practicum.android.diploma.domain.models.filters.FiltersSettings

data class FiltersState(
    val filters: FiltersSettings = FiltersSettings(
        country = "",
        countryId = "",
        region = "",
        regionId = "",
        industry = "",
        industryId = "",
        expectedSalary = "",
        salaryOnlyCheckbox = false
    ),
    val showApply: Boolean = false,
    val showClear: Boolean = false,
)
