package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSelectIndustryBinding
import ru.practicum.android.diploma.domain.models.filters.SubIndustry
import ru.practicum.android.diploma.presentation.IndustryFilterViewModel
import ru.practicum.android.diploma.ui.state.IndustryFilterState
import ru.practicum.android.diploma.util.adapter.industry.IndustryAdapter
import ru.practicum.android.diploma.util.adapter.industry.IndustryAdapterItem

class IndustryFilterFragment : Fragment() {
    private var _binding: FragmentSelectIndustryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<IndustryFilterViewModel>()
    private var currentIndustry: SubIndustry? = null
    private var selectedIndustryId: String? = null
    private var selectedIndustryIndex: Int = -1
    private var adapter = IndustryAdapter { industry ->
        binding.applyButton.isVisible = industry.selected
        currentIndustry = industry.industry
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getIndustries()
        binding.industryRecycleView.adapter = this.adapter
        binding.industryRecycleView.isVisible = true
        binding.industrySearchQuery.doOnTextChanged { query, _, _, _ ->
            viewModel.filterIndustry(query.toString())
            updateUIBasedOnText(query)
        }

        binding.industryFilterToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.applyButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(INDUSTRY_KEY to currentIndustry))
            findNavController().popBackStack()
        }

        viewModel.industryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                IndustryFilterState.Empty -> showEmpty()
                IndustryFilterState.Error -> showError()
                IndustryFilterState.Load -> showLoading()
                is IndustryFilterState.Success -> showContent(data = state.data)
            }
        }
    }

    private fun updateUIBasedOnText(text: CharSequence?) {
        if (text.isNullOrBlank()) {
            binding.clearCrossIc.isVisible = false
            binding.searchIconLoupe.isVisible = true
            if (adapter.checkedRadioButtonId != -1) {
                binding.applyButton.isVisible = true
            }
        } else {
            binding.clearCrossIc.isVisible = true
            binding.searchIconLoupe.isVisible = false
            binding.clearCrossIc.setOnClickListener {
                binding.industrySearchQuery.text?.clear()
            }
        }
    }

    private fun showContent(data: List<IndustryAdapterItem>) {
        binding.industryCenterProgressBar.isVisible = false
        binding.placeholderGroup.isVisible = false
        binding.applyButton.isVisible = false
        binding.industryRecycleView.isVisible = true
        val activeIndex = data.indexOfFirst { it.industry.id == currentIndustry?.id }
        val updatedData = data.mapIndexed { index, item ->
            IndustryAdapterItem(item.industry, index == activeIndex)
        }
        adapter.updateList(updatedData)
        if (activeIndex != -1) {
            binding.applyButton.isVisible = true
            currentIndustry = updatedData[activeIndex].industry
        }
        handleIndustrySelection(updatedData)
    }

    private fun handleIndustrySelection(data: List<IndustryAdapterItem>) {
        if (currentIndustry == null) {
            val industryIdPrefs = arguments?.getString(INDUSTRY_KEY_ID)
            adapter.setSelectedIndustry(industryIdPrefs)
            selectedIndustryId = industryIdPrefs
            selectedIndustryIndex = adapter.checkedRadioButtonId

            industryIdPrefs?.let { id ->
                val position = data.indexOfFirst { it.industry.id == id }
                if (position != -1) {
                    binding.applyButton.isVisible = true
                    currentIndustry = data[position].industry
                }
            }
        }
    }

    private fun showEmpty() {
        showErrorOrEmptyState(R.drawable.no_vacancy_list, R.string.no_such_industry)
    }

    private fun showError() {
        showErrorOrEmptyState(R.drawable.no_such_list_found, R.string.favorite_list_empty)
    }

    private fun showErrorOrEmptyState(image: Int, text: Int) {
        binding.applyButton.isVisible = false
        binding.industryRecycleView.isVisible = false
        binding.industryCenterProgressBar.isVisible = false

        binding.noListPlaceholder.setImageResource(image)
        binding.placeholderText.setText(text)
        binding.placeholderGroup.isVisible = true
    }

    private fun showLoading() {
        binding.industryCenterProgressBar.isVisible = true
        binding.applyButton.isVisible = false
        binding.industryRecycleView.isVisible = false
        binding.placeholderGroup.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "KEY"
        const val INDUSTRY_KEY = "INDUSTRY"
        const val INDUSTRY_KEY_ID = "INDUSTRY_ID"
    }
}
