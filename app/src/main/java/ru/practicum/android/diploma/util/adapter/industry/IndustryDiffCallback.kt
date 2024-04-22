package ru.practicum.android.diploma.util.adapter.industry

import androidx.recyclerview.widget.DiffUtil

class IndustryDiffCallback(
    private val oldList: List<IndustryAdapterItem>,
    private val newList: List<IndustryAdapterItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].industry.id == newList[newItemPosition].industry.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
