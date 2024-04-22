package ru.practicum.android.diploma.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
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
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.domain.models.filters.FiltersSettings
import ru.practicum.android.diploma.domain.models.filters.SubIndustry
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
            }
        }

        setFragmentResultListenerControl()

        initButtonListeners()
        initTextBehaviour()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initButtonListeners() {
        binding.filterToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.applyButton.setOnClickListener { saveAndNavigateUp() }
        binding.clearButton.setOnClickListener { clearFilters() }
        binding.salaryFlagCheckbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSalaryOnlyCheckbox(isChecked)
        }
        binding.industryTextInput.setOnClickListener {
            navigateToIndustry()
        }
        binding.industryLayout.setOnClickListener {
            navigateToIndustry()
        }
        binding.workPlaceLayout.setOnClickListener {
            navigateToPlaceOfWork()
        }
        binding.workTextInput.setOnClickListener {
            navigateToPlaceOfWork()
        }
    }

    private fun initTextBehaviour() {
        binding.salary.doOnTextChanged { text, _, _, _ ->
            viewModel.setExpectedSalary(text?.toString())
            if (text.isNullOrEmpty()) {
                binding.salaryLayout.endIconMode = TextInputLayout.END_ICON_NONE
                binding.salaryLayout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
                if (binding.salary.isFocused) {
                    binding.salaryLayout.defaultHintTextColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.light_blue))
                } else {
                    binding.salaryLayout.defaultHintTextColor =
                        ColorStateList.valueOf(requireContext().getColorOnSecondaryFixed())
                }
            } else {
                if (binding.salary.isFocused) {
                    binding.salaryLayout.defaultHintTextColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.light_blue))
                } else {
                    binding.salaryLayout.defaultHintTextColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.black))
                }
                binding.salaryLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.salaryLayout.setEndIconOnClickListener {
                    binding.salary.setText("")
                }
            }
        }
    }

    @ColorInt
    fun Context.getColorOnSecondaryFixed(): Int {
        val typedValue = TypedValue()
        val theme = theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondaryFixed, typedValue, true)
        return typedValue.data
    }

    fun navigateToPlaceOfWork() {
        val (country, region) = viewModel.getActualCountryAndRegion()
        findNavController().navigate(
            R.id.action_filterFragment_to_filtersPlaceOfWorkFragment,
            bundleOf(
                FiltersCountryFragment.COUNTRY_KEY to country,
                FiltersRegionFragment.REGION_KEY to region
            )
        )
    }

    fun navigateToIndustry() {
        val industryIdPrefs = viewModel.getIndustryId()
        findNavController().navigate(
            R.id.action_filterFragment_to_industry_filer_screen,
            bundleOf(
                IndustryFilterFragment.INDUSTRY_KEY_ID to industryIdPrefs
            )
        )
    }

    private fun initFilterSettings(filterSettings: FiltersSettings) {
        if (binding.salary.isFocused.not() &&
            binding.salary.text?.toString() != filterSettings.expectedSalary
        ) {
            binding.salary.setText(filterSettings.expectedSalary)
        }
        setStateLocation(filterSettings.country, filterSettings.region)
        setStateIndustry(filterSettings.industry)
        binding.salaryFlagCheckbox.isChecked = filterSettings.salaryOnlyCheckbox
    }

    private fun savePrefs() {
        viewModel.savePrefs()
    }

    private fun setStateLocation(country: String?, region: String?) {
        binding.workPlaceLayout.apply {
            setEndIconDrawable(
                if (country.isNullOrEmpty()) {
                    setEndIconOnClickListener {
                        navigateToPlaceOfWork()
                    }
                    R.drawable.ic_arrow_forward_14px
                } else {
                    setEndIconOnClickListener {
                        clearWorkPlace()
                    }
                    R.drawable.ic_close_cross_14px
                }
            )
        }
        val place = country + if (!region.isNullOrEmpty()) {
            getString(R.string.separator) + " $region"
        } else {
            ""
        }
        binding.workTextInput.setText(place)
    }

    private fun setStateIndustry(industry: String?) {
        binding.industryLayout.apply {
            setEndIconDrawable(
                if (industry.isNullOrEmpty()) {
                    setOnClickListener {
                        navigateToIndustry()
                    }
                    R.drawable.ic_arrow_forward_14px
                } else {
                    setEndIconOnClickListener {
                        clearIndustry()
                    }
                    R.drawable.ic_close_cross_14px
                }
            )
        }
        binding.industryTextInput.setText(industry)
    }

    private fun setFragmentResultListenerControl() {
        parentFragmentManager.setFragmentResultListener(
            IndustryFilterFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val industry =
                BundleCompat.getParcelable(bundle, IndustryFilterFragment.INDUSTRY_KEY, SubIndustry::class.java)
            viewModel.setNewIndustry(industry)
        }

        parentFragmentManager.setFragmentResultListener(
            FiltersPlaceOfWorkFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val country =
                BundleCompat.getParcelable(bundle, FiltersPlaceOfWorkFragment.COUNTRY_KEY, Area::class.java)
            val region = BundleCompat.getParcelable(bundle, FiltersPlaceOfWorkFragment.REGION_KEY, Area::class.java)
            viewModel.setNewCounterAndRegion(country, region)
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
            0 -> binding.industryTextInput.isVisible = false
            1 -> binding.workTextInput.isVisible = false
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
}
