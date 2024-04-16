package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentRegionBinding
import ru.practicum.android.diploma.domain.models.filters.Area
import ru.practicum.android.diploma.presentation.RegionViewModel
import ru.practicum.android.diploma.ui.RegionAdapter
import ru.practicum.android.diploma.ui.state.RegionState
import ru.practicum.android.diploma.util.Constants.REGION_BACKSTACK_KEY

class RegionFragment : Fragment() {
    private var _binding: FragmentRegionBinding? = null
    private val binding: FragmentRegionBinding
        get() = _binding!!

    private val viewModel by viewModel<RegionViewModel>()
    private var textWatcher: TextWatcher? = null
    private val adapter = RegionAdapter { region ->
        selectRegion(region)
    }

    companion object {
        const val COUNTRY_ARG = "country"
        fun createArgs(vacancyId: String?): Bundle = bundleOf(COUNTRY_ARG to vacancyId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rwResult.layoutManager = LinearLayoutManager(requireContext())
        binding.rwResult.adapter = adapter

        val area = requireArguments().getSerializable(COUNTRY_ARG) as String

        viewModel.getRegions(area)
        viewModel.state.observe(viewLifecycleOwner, ::renderState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setFilteredRegions(s)
            }

            override fun afterTextChanged(s: Editable?) {
                with(binding.searchTextInputLayout) {
                    if (s.isNullOrBlank()) {
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        setEndIconDrawable(R.drawable.ic_search_loupe_18px)
                        findViewById<View>(com.google.android.material.R.id.text_input_end_icon).isClickable = false
                    } else {
                        setEndIconDrawable(R.drawable.ic_close_cross_14px)
                        setEndIconOnClickListener { s.clear() }
                    }
                }
            }
        }
        binding.etSearch.addTextChangedListener(textWatcher)
    }

    private fun selectRegion(region: Area) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(REGION_BACKSTACK_KEY, region)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(COUNTRY_ARG, region.parentId)
        findNavController().popBackStack()
    }

    private fun setFilteredRegions(s: CharSequence?) {
        if (!s.isNullOrEmpty()) {
            viewModel.getRegionsList()
                ?.let { adapter.setData(it.filter { it.name.lowercase().contains(s) }) }
        } else {
            viewModel.getRegionsList()?.let { adapter.setData(it) }
        }
    }

    private fun renderState(state: RegionState) {
        when (state) {
            is RegionState.Loading -> showLoading()
            is RegionState.Error -> showError()
            is RegionState.Content -> showContent(state.data)
        }
    }

    private fun showLoading() {
        binding.rwResult.isVisible = false
        binding.llPlaceholder.isVisible = false
        binding.pbLoading.isVisible = true
    }

    private fun showError() {
        binding.rwResult.isVisible = false
        binding.llPlaceholder.isVisible = true
        binding.pbLoading.isVisible = false

        binding.ivPlaceholders.setImageDrawable(requireContext().getDrawable(R.drawable.placeholder_get_list))
        binding.tvPlaceholders.text = requireContext().getText(R.string.cant_get_list)
    }

    private fun showContent(data: List<Area>) {
        binding.rwResult.isVisible = true
        binding.llPlaceholder.isVisible = false
        binding.pbLoading.isVisible = false

        adapter.clear()
        adapter.setData(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
