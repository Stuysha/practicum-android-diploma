package ru.practicum.android.diploma.util.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.SearchItemViewBinding
import ru.practicum.android.diploma.domain.models.VacancyModel
import ru.practicum.android.diploma.util.adapter.ConvertCurrency.converterSalaryToString

class SearchItemViewHolder(
    private val binding: SearchItemViewBinding,
    private val onClick: (VacancyModel) -> Unit,
    private val onLongClick: (VacancyModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: VacancyModel) {
        with(binding) {
            if (item.area?.name?.isNotBlank() == true) {
                val title = buildString {
                    append(item.name)
                    append(",")
                    append(item.area.name)
                }
                positionTitle.text = title
            } else {
                positionTitle.text = item.name
            }

            companyTitle.text = item.employer?.name
            salaryTitle.text = converterSalaryToString(item.salary?.from, item.salary?.to, item.salary?.currency)

            root.setOnClickListener { onClick(item) }
            root.setOnLongClickListener {
                onLongClick(item)
                true
            }

            Glide.with(itemView)
                .load(item.employer?.logoUrls?.logo90)
                .placeholder(R.drawable.ic_placeholder_30px)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        itemView.resources.getDimensionPixelSize(R.dimen.dimen_12dp)
                    )
                )
                .into(itemLogo)
        }
    }
}
