package ru.practicum.android.diploma.util.adapter.industry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.IndustryItemBinding
import ru.practicum.android.diploma.domain.models.filters.SubIndustry

class IndustryAdapter(private val onClick: (IndustryAdapterItem) -> Unit) : RecyclerView.Adapter<IndustryViewHolder>() {
    var data: List<IndustryAdapterItem> = emptyList()
    var checkedRadioButtonId: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        return IndustryViewHolder(IndustryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item.industry)
        holder.binding.roundButton.isChecked = item.selected
        holder.binding.root.setOnClickListener {
            updateSelectedIndustry(position)
        }
        holder.binding.roundButton.setOnClickListener {
            updateSelectedIndustry(position)
        }
    }

    private fun updateSelectedIndustry(position: Int) {
        val oldData = ArrayList(data)
        val updatedData = data.mapIndexed { index, item ->
            IndustryAdapterItem(item.industry, index == position)
        }
        val diffCallback = IndustryDiffCallback(oldData, updatedData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = updatedData
        checkedRadioButtonId = position
        onClick.invoke(data[position])
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateList(newList: List<IndustryAdapterItem>) {
        val diffCallback = IndustryDiffCallback(data, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = newList
        diffResult.dispatchUpdatesTo(this)
        notifyItemRangeChanged(0, data.size)
    }

    fun setSelectedIndustry(industryId: String?) {
        val position = data.indexOfFirst { it.industry.id == industryId }
        if (position != -1) {
            val updatedData = data.mapIndexed { index, item ->
                IndustryAdapterItem(item.industry, index == position)
            }
            val diffCallback = IndustryDiffCallback(data, updatedData)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            data = updatedData
            checkedRadioButtonId = position
            diffResult.dispatchUpdatesTo(this)
        }
    }
}

data class IndustryAdapterItem(
    val industry: SubIndustry,
    val selected: Boolean = false
)
