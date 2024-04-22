package ru.practicum.android.diploma.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ChooseCountryItemBinding
import ru.practicum.android.diploma.domain.models.fields.AreaModel

class CountryAdapter(private val onClick: (AreaModel) -> Unit) :
    RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var data = emptyList<AreaModel>()

    inner class CountryViewHolder(private val binding: ChooseCountryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AreaModel) {
            binding.text.text = item.name
            binding.root.setOnClickListener { onClick.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        return CountryViewHolder(
            ChooseCountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(list: List<AreaModel>) {
        data = list
        notifyDataSetChanged()
    }
}
