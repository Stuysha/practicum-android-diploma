package ru.practicum.android.diploma.util.adapter.filter

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.CountryBinding
import ru.practicum.android.diploma.domain.models.filters.Area

class RegionViewHolder(private val binding: CountryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Area) {
        binding.country.text = item.name
    }
}
