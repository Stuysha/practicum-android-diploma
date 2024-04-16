package ru.practicum.android.diploma.data.dto.fields

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.data.network.api.Response

data class RegionResponseDto(
    val areas: List<RegionResponseDto>,
    val id: String? = null,
    val name: String? = null,
    @SerializedName("parent_id") val parentId: String? = null
) : Response()
