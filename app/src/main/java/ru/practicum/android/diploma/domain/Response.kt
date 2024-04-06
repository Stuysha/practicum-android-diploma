package ru.practicum.android.diploma.domain

import ru.practicum.android.diploma.util.ErrorVariant

sealed class Response<T : Any> {
    class Success<T : Any>(val data: T) : Response<T>()
    class Error<T : Any>(val error: ErrorVariant) : Response<T>()
}
