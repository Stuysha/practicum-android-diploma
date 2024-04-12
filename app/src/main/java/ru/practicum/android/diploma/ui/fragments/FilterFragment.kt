package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings
import ru.practicum.android.diploma.presentation.FilterViewModel

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FilterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.filterState.collect {
                initFilterSettings(it.filters)
                applyButtonVisibility(it.showApply)
                clearButtonVisibility(it.showClear)
                clearButtonSalaryVisibility()
            }
        }
        initButtonListeners()
        initTextBehaviour()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initButtonListeners() {
        binding.filterToolbar.setNavigationOnClickListener { saveAndNavigateUp() }
        binding.applyButton.setOnClickListener { saveAndNavigateUp() }
        binding.clearButton.setOnClickListener { clearFilters() }
        binding.salaryFlagCheckbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSalaryOnlyCheckbox(isChecked)
        }
    }

    private fun initTextBehaviour() {
        binding.salary.doOnTextChanged { text, _, _, _ ->
            viewModel.setExpectedSalary(text?.toString()?.toIntOrNull())
            if (text.isNullOrEmpty()) {
                binding.salaryLayout.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                binding.salaryLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.salaryLayout.setEndIconOnClickListener {
                    binding.salary.setText("")
                }
            }
        }
    }

    private fun initFilterSettings(filterSettings: FiltersSettings) {
        setStateLocation(filterSettings.country, filterSettings.region)
        setStateIndustry(filterSettings.industry)
        binding.salaryFlagCheckbox.isChecked = filterSettings.salaryOnlyCheckbox
    }

    private fun savePrefs() {
        viewModel.savePrefs()
    }

    private fun setStateLocation(country: String?, region: String?) {
        binding.workTextInput.apply {
            visibility = if (country.isNullOrEmpty()) View.GONE else View.VISIBLE
            setOnClickListener { clearWorkPlace() }
        }
        binding.workPlaceLayout.apply {
            setEndIconDrawable(if (country.isNullOrEmpty()) R.drawable.ic_arrow_forward_14px else R.drawable.ic_close_cross_14px)
        }
        val place = country + if (!region.isNullOrEmpty()) {
            getString(R.string.separator) + region
        } else {
            ""
        }
        binding.workTextInput.setText(place)
    }

    private fun setStateIndustry(industry: String?) {
        binding.industryTextInput.apply {
            visibility = if (industry.isNullOrEmpty()) View.GONE else View.VISIBLE
            setOnClickListener { clearIndustry() }
        }
        binding.industryLayout.apply {
            setEndIconDrawable(if (industry.isNullOrEmpty()) R.drawable.ic_arrow_forward_14px else R.drawable.ic_close_cross_14px)
        }
    }

    private fun clearWorkPlace() {
        viewModel.setNewRegionCountry(region = null, country = null, countryId = null, regionId = null)
        clearArguments(1)
    }

    private fun clearIndustry() {
        viewModel.setNewIndustry(null)
        clearArguments(0)
    }

    private fun clearFilters() {
        binding.apply {
            clearButton.isVisible = false
            salary.setText("")
        }
        viewModel.clearPrefs()
    }

    private fun clearArguments(type: Int) {
        when (type) {
            0 -> binding.industryLayout.isVisible = false
            1 -> binding.workPlaceLayout.isVisible = false
        }
    }

    private fun saveAndNavigateUp() {
        lifecycleScope.launch(Dispatchers.IO) {
            savePrefs()
            withContext(Dispatchers.Main) {
                findNavController().navigateUp()
            }
        }
    }

    private fun applyButtonVisibility(visible: Boolean) {
        binding.applyButton.isVisible = visible
    }

    private fun clearButtonVisibility(visible: Boolean) {
        binding.clearButton.isVisible = visible
    }

    private fun clearButtonSalaryVisibility() {
        binding.clearButton.isVisible = binding.salary.text?.isEmpty() == false
    }
}
