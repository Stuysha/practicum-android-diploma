package ru.practicum.android.diploma.data.dto.fields

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.data.network.api.Response

data class AreaDto(
    val id: String,
    val name: String?,
    @SerializedName("parent_id")
    val parentId: String?,
    @SerializedName("country_id")
    val countryId: String?,
    val areas: List<AreaDto>?
) : Response()
