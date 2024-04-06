package ru.practicum.android.diploma.util

import ru.practicum.android.diploma.util.Constants.BAD_REQUEST_ERROR
import ru.practicum.android.diploma.util.Constants.NO_CONNECTION_ERROR

enum class ErrorVariant(val code: Int) {
    NO_CONNECTION(NO_CONNECTION_ERROR),
    BAD_REQUEST(BAD_REQUEST_ERROR),
    NO_CONTENT(Int.MIN_VALUE)
}
