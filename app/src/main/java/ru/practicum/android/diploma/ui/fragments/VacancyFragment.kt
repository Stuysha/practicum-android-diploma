package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.VacancyDetailsModel
import ru.practicum.android.diploma.domain.models.fields.KeySkillsModel
import ru.practicum.android.diploma.domain.models.fields.PhonesModel
import ru.practicum.android.diploma.presentation.VacancyViewModel
import ru.practicum.android.diploma.ui.state.VacancyDetailsScreenState
import ru.practicum.android.diploma.util.Constants.BUNDLE_KEY_VACANCY_ID
import ru.practicum.android.diploma.util.ErrorVariant
import ru.practicum.android.diploma.util.adapter.ConvertCurrency

class VacancyFragment : Fragment() {

    private val viewModel by viewModel<VacancyViewModel>()
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vacancyToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        var vacancyId: String = ""
        val bundle = arguments
        if (bundle != null) {
            vacancyId = bundle.getString(BUNDLE_KEY_VACANCY_ID).toString()
            viewModel.fetchDetails(vacancyId)
            binding.loadingProgressBar.isVisible = false
        } else {
            binding.loadingProgressBar.isVisible = true
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.favoriteVacanciesState.observe(viewLifecycleOwner) {
            val image = if (it) {
                R.drawable.ic_favorites_on_20px
            } else {
                R.drawable.ic_favorites_off_23px
            }
            binding.vacancyFavoriteIcon.setImageResource(image)
        }

        binding.vacancyFavoriteIcon.setOnClickListener {
            viewModel.onLikeClick()
        }

        binding.contactsPhone.setOnClickListener {
            viewModel.dialPhone(binding.contactsPhone.text.toString().trim())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun render(state: VacancyDetailsScreenState) {
        when (state) {
            is VacancyDetailsScreenState.Content -> {
                binding.noConnectionText.visibility = View.GONE
                binding.loadingProgressBar.visibility = View.GONE
                binding.serverErrorPlaceholder.visibility = View.GONE
                binding.vacancyDetailsLayout.visibility = View.VISIBLE
                addViews(state.vacancyDetails)
            }

            is VacancyDetailsScreenState.Error -> {
                showError(state.errorVariant)
            }

            is VacancyDetailsScreenState.Loading -> {
                showLoading()
            }
        }
    }

    private fun showError(errorVariant: ErrorVariant) {
        with(binding) {
            loadingProgressBar.isVisible = false
            vacancyDetailsLayout.isVisible = false
            descriptionTitle.isVisible = false
            serverErrorPlaceholder.isVisible = true
            noConnectionText.isVisible = true
        }
    }

    private fun showLoading() {
        with(binding) {
            loadingProgressBar.isVisible = true
        }
    }

    private fun addViews(vacancy: VacancyDetailsModel) {
        with(binding) {
            vacancyName.text = vacancy.name ?: getString(R.string.no_information)
            area.text = vacancy.area?.name
            vacancySalary.text = ConvertCurrency.converterSalaryToString(
                vacancy.salary?.from,
                vacancy.salary?.to,
                vacancy.salary?.currency
            )
            setLogo(vacancy)
            companyName.text = vacancy.employer?.name
            experience.text = vacancy.experience?.name
            scheduleEmployment.text = vacancy.schedule?.name
            dutiesSubtitle.text = HtmlCompat.fromHtml(
                vacancy.description?.addSpacesAfterLiTags() ?: "",
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
            ).trim()
            coreSkills.text = HtmlCompat.fromHtml(
                getKeySkillsText(vacancy.keySkills),
                HtmlCompat.FROM_HTML_MODE_COMPACT
            ).trim()
            contactsName.text = vacancy.contacts?.name
            contactEmail.text = vacancy.contacts?.email
            contactsPhone.text = getPhonesText(vacancy.contacts?.phones)
            contactsComment.text = getPhonesCommentsText(vacancy.contacts?.phones)
            showFields()
            hideGroups(vacancy)

            binding.vacancyShareIcon.setOnClickListener {
                if (vacancy.alternateUrl != null) viewModel.vacancyShare(vacancy.alternateUrl)
            }
            binding.contactEmail.setOnClickListener {
                viewModel.sendEmail(
                    email = vacancy.contacts?.email.toString(),
                    subject = getString(R.string.email_subject)
                )
            }
        }
    }

    private fun setLogo(vacancy: VacancyDetailsModel) {
        Glide.with(this@VacancyFragment)
            .load(vacancy.employer?.logoUrls?.logo90)
            .placeholder(R.drawable.ic_placeholder_30px)
            .centerCrop()
            .transform(
                RoundedCorners(
                    this@VacancyFragment.resources.getDimensionPixelSize(
                        R.dimen.dimen_12dp
                    )
                )
            )
            .into(binding.employerLogo)
    }

    private fun hideGroups(vacancy: VacancyDetailsModel) {
        with(binding) {
            if (
                vacancy.contacts?.name.isNullOrEmpty() &&
                vacancy.contacts?.email.isNullOrEmpty() &&
                vacancy.contacts?.phones.isNullOrEmpty()
            ) {
                groupContacts.visibility = View.GONE
                contactsGroup.visibility = View.GONE
            } else {
                groupContacts.visibility = View.VISIBLE
                contactsGroup.visibility = View.VISIBLE
            }
            contactsEmailGroup.isVisible = !vacancy.contacts?.email.isNullOrBlank()
            contactsPhoneGroup.isVisible = vacancy.contacts?.phones?.isNotEmpty() ?: false
            contactsCommentGroup.isVisible =
                !vacancy.contacts?.phones?.firstOrNull()?.comment.isNullOrBlank()
            contactsPersonGroup.isVisible = !vacancy.contacts?.name.isNullOrBlank()
            keySkillsGroup.isVisible = !vacancy.keySkills.isNullOrEmpty()
        }
    }

    private fun String.addSpacesAfterLiTags(): String {
        return this.replace(Regex("<li>\\s<p>|<li>"), "<li>\u00A0")
    }

    private fun showFields() {
        binding.cardView.isVisible = true
        binding.experienceTitle.isVisible = true
        binding.scheduleEmployment.isVisible = true
        binding.descriptionTitle.isVisible = true
    }

    private fun getKeySkillsText(keySkills: List<KeySkillsModel>?): String {
        var text = "<ul>"
        keySkills?.forEach { text += "<li>\u00A0 ${it.name}</li>\n" }
        text += "</ul>"
        return text
    }

    private fun getPhonesText(phones: List<PhonesModel>?): String {
        var text = ""
        phones?.forEach { text += "+${it.countryCode} (${it.cityCode}) ${it.number}\n" }
        return text.trimEnd()
    }

    private fun getPhonesCommentsText(phones: List<PhonesModel>?): String {
        var text = ""
        phones?.forEach { text += "${it.comment}\n" }
        return text.trimEnd()
    }
}
