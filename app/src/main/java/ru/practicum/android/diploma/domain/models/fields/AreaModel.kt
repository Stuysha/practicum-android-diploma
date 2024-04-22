package ru.practicum.android.diploma.domain.models.fields

import java.io.Serializable

data class AreaModel(
    val id: String?,
    val name: String?,
    val countryId: String?,
    val areas: List<AreaModel>?
) : Serializable
