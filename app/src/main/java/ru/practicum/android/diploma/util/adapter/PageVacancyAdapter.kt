package ru.practicum.android.diploma.util.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.practicum.android.diploma.databinding.SearchItemViewBinding
import ru.practicum.android.diploma.domain.models.VacancyModel

class PageVacancyAdapter(
    val clickItem: (VacancyModel) -> Unit
) : PagingDataAdapter<VacancyModel, SearchItemViewHolder>(ArticleDiffItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SearchItemViewHolder(SearchItemViewBinding.inflate(layoutInflater, parent, false), clickItem, {})
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        getItem(position)?.let { vacancy ->
            holder.bind(vacancy)
            holder.itemView.setOnClickListener { clickItem(vacancy) }
        }

    }
}

private object ArticleDiffItemCallback : DiffUtil.ItemCallback<VacancyModel>() {
    override fun areItemsTheSame(oldItem: VacancyModel, newItem: VacancyModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: VacancyModel, newItem: VacancyModel): Boolean {
        return true
    }
}
