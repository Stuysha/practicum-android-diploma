package ru.practicum.android.diploma.data.dto.fields

import com.google.gson.annotations.SerializedName

data class PhonesDto(
    @SerializedName("city")
    val cityCode: String?,
    val comment: String?,
    @SerializedName("country")
    val countryCode: String?,
    val formatted: String?,
    val number: String?
)
