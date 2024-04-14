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
        checkedRadioButtonId = position
        data[position].selected = true
        onClick.invoke(data[position])
        notifyItemChanged(position)
        val oldPosition = data.indexOfFirst { it != data[position] && it.selected }
        if (oldPosition > -1) {
            data[oldPosition].selected = false
            notifyItemChanged(oldPosition)
        }
    }

    fun updateList(newList: List<IndustryAdapterItem>) {
        val diffCallback = IndustryDiffCallback(data, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setSelectedIndustry(industryId: String?) {
        val position = data.indexOfFirst { it.industry.id == industryId }
        if (position != -1) {
            data[position].selected = true
            checkedRadioButtonId = position
            notifyItemChanged(position)
        }
    }
}

data class IndustryAdapterItem(
    val industry: SubIndustry,
    var selected: Boolean = false
)
