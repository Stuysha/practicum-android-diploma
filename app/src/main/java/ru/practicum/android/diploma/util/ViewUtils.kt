package ru.practicum.android.diploma.util

import android.view.View

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
