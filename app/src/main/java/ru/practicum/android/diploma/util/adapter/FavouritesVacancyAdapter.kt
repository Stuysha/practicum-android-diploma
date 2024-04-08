package ru.practicum.android.diploma.util.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.SearchItemViewBinding
import ru.practicum.android.diploma.domain.models.VacancyDetailsModel

class FavouritesVacancyAdapter(private val onClick: (VacancyDetailsModel) -> Unit) :
    RecyclerView.Adapter<FavouritesVacancyAdapter.FavouriteVacancyViewHolder>() {

    private var list = emptyList<VacancyDetailsModel>()

    inner class FavouriteVacancyViewHolder(private val binding: SearchItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VacancyDetailsModel) {
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
                salaryTitle.text =
                    ConvertCurrency.converterSalaryToString(item.salary?.from, item.salary?.to, item.salary?.currency)

                root.setOnClickListener { onClick(item) }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteVacancyViewHolder {
        return FavouriteVacancyViewHolder(
            SearchItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FavouriteVacancyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(data: List<VacancyDetailsModel>) {
        list = data
        notifyDataSetChanged()
    }
}
