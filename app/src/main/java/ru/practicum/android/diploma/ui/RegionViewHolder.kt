package ru.practicum.android.diploma.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.filters.Area

class RegionViewHolder(
    parent: ViewGroup,
    private val clickListener: RegionAdapter.RegionClickListener
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.card_region, parent, false)
) {
    private val place = itemView.findViewById<TextView>(R.id.place_name)

    fun bind(region: Area) {
        place.text = region.name

        itemView.setOnClickListener {
            clickListener.onClick(region)
        }
    }
}
