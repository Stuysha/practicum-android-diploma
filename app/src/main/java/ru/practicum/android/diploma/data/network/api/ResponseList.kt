package ru.practicum.android.diploma.data.network.api

data class ResponseList<T>(
    val data: T?
) : Response()
