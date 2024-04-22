package ru.practicum.android.diploma.domain.models.filters

class CountrySortOrder {

    val customOrder = listOf(
        "Россия",
        "Украина",
        "Казахстан",
        "Азербайджан",
        "Беларусь",
        "Грузия",
        "Кыргызстан",
        "Узбекистан",
        "Другие регионы"
    )

    companion object {
        fun sortCountriesListManually(countries: List<Area>): List<Area> {
            val sortOrder = CountrySortOrder()
            return countries.sortedBy { sortOrder.customOrder.indexOf(it.name) }
        }
    }
}
