package ru.practicum.android.diploma.domain.models

import android.content.Context
import androidx.annotation.StringRes

class MessageData {
    private var string: String? = null

    @StringRes
    private var idString: Int? = null

    constructor(message: String) {
        string = message
    }

    constructor(@StringRes idString: Int) {
        this.idString = idString
    }

    fun getString(context: Context?): String? {
        return if (string != null) {
            string
        } else {
            idString?.let {
                context?.getString(it)
            }
        }
    }
}
