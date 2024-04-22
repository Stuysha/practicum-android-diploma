package ru.practicum.android.diploma.data.mapper

import ru.practicum.android.diploma.data.dto.fields.AreaDto
import ru.practicum.android.diploma.data.dto.fields.IndustryDto
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.Industry
import ru.practicum.android.diploma.domain.models.filters.SubIndustry

fun AreaDto.convertorToArea(): Area {
    return Area(
        id = id,
        name = name ?: "",
        parentId = parentId ?: "",
        areas = areas?.map { it.convertorToArea() } ?: emptyList()
    )
}

fun IndustryDto.convertorToIndustry(): Industry {
    val subindustries: MutableList<SubIndustry> = mutableListOf()
    for (subindustry in industries) {
        subindustries.add(
            SubIndustry(
                id = subindustry.id,
                name = subindustry.name
            )
        )
    }
    return Industry(
        id = id,
        name = name ?: "",
        industries = subindustries
    )
}
