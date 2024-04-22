package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterChooseRegionBinding
import ru.practicum.android.diploma.domain.models.filters.RegionDataItem
import ru.practicum.android.diploma.presentation.FiltersRegionViewModel
import ru.practicum.android.diploma.ui.state.FiltersRegionsState
import ru.practicum.android.diploma.util.adapter.filter.RegionAdapter
import ru.practicum.android.diploma.util.gone
import ru.practicum.android.diploma.util.visible

class FiltersRegionFragment : Fragment() {

    private var _binding: FragmentFilterChooseRegionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FiltersRegionViewModel by viewModel()
    private var regionsAdapter: RegionAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterChooseRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.init(arguments?.getString(FiltersCountryFragment.COUNTRY_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.filtersRegionStateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

        regionsAdapter = RegionAdapter { region ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    REGION_KEY to region.currentRegion,
                    FiltersCountryFragment.COUNTRY_KEY to region.rootRegion
                )
            )
            findNavController().popBackStack()
        }

        binding.regionList.adapter = regionsAdapter
        binding.regionList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        setListener()
    }

    fun setListener() {
        binding.choosingRegion.doAfterTextChanged {
            if (it?.isNotEmpty() == true) {
                binding.imageTextView.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.ic_clear_button
                    )
                })
                viewModel.findArea(it.toString() ?: "")
            } else {
                binding.imageTextView.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.ic_search
                    )
                })
                viewModel.showAllArea()
            }

        }

        binding.arrowBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.imageTextView.setOnClickListener {
            binding.choosingRegion.setText("")
            viewModel.showAllArea()
        }

        binding.choosingRegion.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = context?.let { getSystemService(it, InputMethodManager::class.java) }
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun render(state: FiltersRegionsState) {
        when (state) {
            is FiltersRegionsState.Content -> showContent(state.regions)
            is FiltersRegionsState.Empty -> showEmpty()
            is FiltersRegionsState.Error -> showError()
            is FiltersRegionsState.Loading -> showLoading()
            is FiltersRegionsState.Start -> showStartScreen()
        }
    }

    fun showStartScreen() {
        binding.progressBar.gone()
        binding.placeholderImage.gone()
        binding.placeholderText.gone()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = true
    }

    private fun showLoading() {
        binding.progressBar.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = false
    }

    private fun showError() {
        binding.progressBar.gone()
        binding.placeholderImage.setImageResource(R.drawable.no_such_list_found)
        binding.placeholderText.setText(R.string.failed_to_get_list)
        binding.placeholderImage.visible()
        binding.placeholderText.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = false
    }

    private fun showEmpty() {
        binding.progressBar.gone()
        binding.placeholderImage.setImageResource(R.drawable.no_vacancy_list)
        binding.placeholderText.setText(R.string.no_region)
        binding.placeholderImage.visible()
        binding.placeholderText.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = true
    }

    private fun showContent(regions: List<RegionDataItem>) {
        binding.progressBar.gone()
        binding.placeholderImage.gone()
        binding.placeholderText.gone()
        regionsAdapter!!.setItems(regions)
        binding.regionList.visible()
        binding.choosingRegion.isEnabled = true
    }

    companion object {
        const val REQUEST_KEY = "REGION_KEY"
        const val REGION_KEY = "REGION"
    }
}
