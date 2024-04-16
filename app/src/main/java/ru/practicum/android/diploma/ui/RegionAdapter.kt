package ru.practicum.android.diploma.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.domain.models.filters.Area

class RegionAdapter(
    private val clickListener: RegionClickListener
) : RecyclerView.Adapter<RegionViewHolder>() {

    private val regions = arrayListOf<Area>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RegionViewHolder(parent, clickListener)

    override fun getItemCount() = regions.size
    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(regions[position])
    }

    fun interface RegionClickListener {
        fun onClick(data: Area)
    }

    fun setData(data: List<Area>) {
        regions.clear()
        regions.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        regions.clear()
        notifyDataSetChanged()
    }
}
