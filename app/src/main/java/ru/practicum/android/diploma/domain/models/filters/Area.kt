package ru.practicum.android.diploma.domain.models.filters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Area(
    val id: String,
    val parentId: String?,
    val name: String,
    val areas: List<Area>
) : Parcelable
