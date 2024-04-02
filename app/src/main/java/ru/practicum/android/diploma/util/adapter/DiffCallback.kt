package ru.practicum.android.diploma.util.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.practicum.android.diploma.domain.models.VacancyModel

class DiffCallback(
    private val oldList: List<VacancyModel>,
    private val newList: List<VacancyModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldVacancy = oldList[oldItemPosition]
        val newVacancy = newList[newItemPosition]
        return oldVacancy.id == newVacancy.id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldVacancy = oldList[oldItemPosition]
        val newVacancy = newList[newItemPosition]
        return oldVacancy == newVacancy
    }
}

