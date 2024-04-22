package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFilterChooseCountryBinding
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.presentation.FiltersCountryViewModel
import ru.practicum.android.diploma.ui.state.FiltersCountriesState
import ru.practicum.android.diploma.util.adapter.filter.FilterAdapter
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.visible

class FiltersCountryFragment : Fragment() {

    private var _binding: FragmentFilterChooseCountryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FiltersCountryViewModel by viewModel()
    private var countriesAdapter: FilterAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterChooseCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fillData()

        viewModel.getFiltersCountriesStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        countriesAdapter = FilterAdapter { country ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(COUNTRY_KEY to country))
            findNavController().popBackStack()
        }

        binding.countryList.adapter = countriesAdapter
        binding.countryList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.arrowBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun render(state: FiltersCountriesState) {
        when (state) {
            is FiltersCountriesState.Content -> showContent(state.countries)
            is FiltersCountriesState.Empty -> showEmpty()
            is FiltersCountriesState.Error -> showError()
            is FiltersCountriesState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.progressBar.visible()
    }

    private fun showError() {
        binding.progressBar.gone()
        binding.errorFailedGet.visible()
    }

    private fun showEmpty() {
        binding.progressBar.gone()
        binding.errorFailedGet.visible()
    }

    private fun showContent(countries: List<Area>) {
        binding.progressBar.gone()
        binding.errorFailedGet.gone()
        countriesAdapter!!.countriesList.clear()
        countriesAdapter!!.countriesList.addAll(countries)
        countriesAdapter!!.notifyDataSetChanged()

    }

    companion object {
        const val REQUEST_KEY = "COUNTRY_KEY"
        const val COUNTRY_KEY = "COUNTRY"
    }
}
