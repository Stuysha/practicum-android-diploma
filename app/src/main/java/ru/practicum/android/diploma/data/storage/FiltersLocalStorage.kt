package ru.practicum.android.diploma.data.storage

import android.content.SharedPreferences
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings

class FiltersLocalStorage(private val sharedPreferences: SharedPreferences) : FiltersStorage {

    companion object {
        const val FILTERS_COUNTRY = "FILTERS_COUNTRY"
        const val FILTERS_COUNTRY_ID = "FILTERS_COUNTRY_ID"
        const val FILTERS_REGION = "FILTERS_REGION"
        const val FILTERS_REGION_ID = "FILTERS_REGION_ID"
        const val FILTERS_INDUSTRY = "FILTERS_INDUSTRY"
        const val FILTERS_INDUSTRY_ID = "FILTERS_INDUSTRY_ID"
        const val FILTERS_SALARY = "FILTERS_SALARY"
        const val FILTERS_SALARY_ONLY = "FILTERS_SALARY_ONLY"
    }

    override fun getPrefs(): FiltersSettings {
        return FiltersSettings(
            sharedPreferences.getString(FILTERS_COUNTRY, "") ?: "",
            sharedPreferences.getString(FILTERS_COUNTRY_ID, "") ?: "",
            sharedPreferences.getString(FILTERS_REGION, "") ?: "",
            sharedPreferences.getString(FILTERS_REGION_ID, "") ?: "",
            sharedPreferences.getString(FILTERS_INDUSTRY, "") ?: "",
            sharedPreferences.getString(FILTERS_INDUSTRY_ID, "") ?: "",
            sharedPreferences.getString(FILTERS_SALARY, "") ?: "",
            sharedPreferences.getBoolean(FILTERS_SALARY_ONLY, false),
        )
    }

    override fun savePrefs(settings: FiltersSettings) {
        with(sharedPreferences.edit()) {
            putString(FILTERS_COUNTRY, settings.country)
            putString(FILTERS_COUNTRY_ID, settings.countryId)
            putString(FILTERS_REGION, settings.region)
            putString(FILTERS_REGION_ID, settings.regionId)
            putString(FILTERS_INDUSTRY, settings.industry)
            putString(FILTERS_INDUSTRY_ID, settings.industryId)
            putString(FILTERS_SALARY, settings.expectedSalary)
            putBoolean(FILTERS_SALARY_ONLY, settings.salaryOnlyCheckbox)
        }
    }

    override fun clearPrefs() {
        sharedPreferences.edit().remove(FILTERS_COUNTRY).apply()
        sharedPreferences.edit().remove(FILTERS_COUNTRY_ID).apply()
        sharedPreferences.edit().remove(FILTERS_REGION).apply()
        sharedPreferences.edit().remove(FILTERS_REGION_ID).apply()
        sharedPreferences.edit().remove(FILTERS_INDUSTRY).apply()
        sharedPreferences.edit().remove(FILTERS_INDUSTRY_ID).apply()
        sharedPreferences.edit().remove(FILTERS_SALARY).apply()
        sharedPreferences.edit().remove(FILTERS_SALARY_ONLY).apply()
    }
}
