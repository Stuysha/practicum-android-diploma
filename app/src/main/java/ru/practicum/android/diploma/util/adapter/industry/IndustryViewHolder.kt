package ru.practicum.android.diploma.util.adapter.industry

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.practicum.android.diploma.databinding.IndustryItemBinding
import ru.practicum.android.diploma.domain.models.filters.SubIndustry

class IndustryViewHolder(val binding: IndustryItemBinding) : ViewHolder(binding.root) {
    fun bind(item: SubIndustry) {
        binding.industryUnit.text = item.name
    }
}
