package ru.practicum.android.diploma.ui.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.presentation.SearchViewModel
import ru.practicum.android.diploma.ui.state.SearchScreenState
import ru.practicum.android.diploma.util.Constants.BUNDLE_KEY_VACANCY_ID
import ru.practicum.android.diploma.util.ErrorVariant
import ru.practicum.android.diploma.util.adapter.PageVacancyAdapter
import ru.practicum.android.diploma.util.adapter.SearchLoadStateAdapter

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()
    private var adapter: PageVacancyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vacancyList = binding.searchRecycleView
        adapter = adapter ?: PageVacancyAdapter { actionOnClick(it.id) }.apply {
            this.addLoadStateListener(viewModel::listener)
            viewModel.lastQuery = null
        }

        vacancyList.adapter = adapter?.withLoadStateFooter(footer = SearchLoadStateAdapter())
        vacancyList.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            viewModel.stateVacancyData.collectLatest {
                createNewAdapter()
                adapter?.submitData(it)
            }
        }
        viewModel.searchState.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.searchQuery.doOnTextChanged { query, _, _, _ ->
            if (query.isNullOrEmpty()) {
                binding.searchIconLoupe.isVisible = true
                binding.clearCrossIc.isVisible = false
            } else {
                binding.searchIconLoupe.isVisible = false
                binding.clearCrossIc.isVisible = true
            }

            if (binding.searchQuery.hasFocus() && query.toString().isNotEmpty()) {
                showBlank(query.toString().isEmpty())
            }
            viewModel.onSearchQueryChange(query.toString())
        }

        settingListener()
    }

    fun createNewAdapter() {
        if (viewModel.lastQuery != null) {
            adapter = PageVacancyAdapter { actionOnClick(it.id) }.apply {
                this.addLoadStateListener(viewModel::listener)
            }

            binding.searchRecycleView.adapter = adapter?.withLoadStateFooter(
                footer = SearchLoadStateAdapter(),
            )
        }
    }

    fun settingListener() {
        binding.searchQuery.setOnEditorActionListener { v, actionId, event ->
            val isEnterKeyPressed = actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            if (isEnterKeyPressed) {
                hideKeyboard(v)
                true
            } else {
                false
            }
        }

        binding.searchQuery.requestFocus()
        binding.clearCrossIc.setOnClickListener {
            viewModel.onSearchQueryChange(null)
            clearSearch()
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, it.getString(context) ?: "", Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
        binding.filterIc.setOnClickListener {
            findNavController().navigate(R.id.filterFragment)
        }
        lifecycleScope.launch {
            viewModel.stateFilters.collect {
                if (context != null) {
                    if (!it) {
                        binding.filterIc.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_filter_on_24px
                            )
                        )
                    } else {
                        binding.filterIc.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_filter12px
                            )
                        )
                    }
                }
            }
        }
    }

    private fun clearSearch() {
        binding.searchQuery.setText("")
        val view = requireActivity().currentFocus
        if (view != null) {
            hideKeyboard(view)
        }
        showBlank()
    }

    private fun hideKeyboard(view: View) {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Default -> showBlank()
            is SearchScreenState.Loading -> showProgress()
            is SearchScreenState.Success -> showSearchResult(state.found)
            is SearchScreenState.NothingFound -> showSearchResult(state.found)
            is SearchScreenState.Error -> showNoConnectionError(state.errorVariant)
            is SearchScreenState.LoadNextPage -> showBlank()
        }
    }

    private fun showSearchResult(found: Int) {
        with(binding) {
            searchRecycleView.isVisible = true
            centerProgressBar.isVisible = false
            textUnderSearch.isVisible = false
            searchDefaultPlaceholder.isVisible = false
            noConnectionPlaceholder.isVisible = false
        }
        binding.textUnderSearch.isVisible = true
        binding.textUnderSearch.text = resources.getQuantityString(R.plurals.vacancy_count, found, found)
        binding.noVacancyToShow.isVisible = false
        binding.searchRecycleView.isVisible = true
        binding.badRequest.isVisible = false
        binding.badRequestText.isVisible = false
    }

    private fun showNoConnectionError(errorVariant: ErrorVariant) {
        with(binding) {
            searchRecycleView.isVisible = false
            centerProgressBar.isVisible = false
            textUnderSearch.isVisible = false
            searchDefaultPlaceholder.isVisible = false
            noVacancyToShow.isVisible = false
            noConnectionPlaceholder.isVisible = false
            noConnectionText.isVisible = false
            noVacancyToShowText.isVisible = false
            binding.badRequest.isVisible = false
            binding.badRequestText.isVisible = false
        }
        when (errorVariant) {
            ErrorVariant.NO_CONNECTION -> {
                binding.noConnectionPlaceholder.isVisible = true
                binding.noConnectionText.isVisible = true
            }

            ErrorVariant.BAD_REQUEST -> {
                binding.badRequest.isVisible = true
                binding.badRequestText.isVisible = true
            }

            ErrorVariant.NO_CONTENT -> {
                binding.noVacancyToShowText.isVisible = true
                binding.noVacancyToShow.isVisible = true
                binding.textUnderSearch.isVisible = true
                binding.textUnderSearch.setText(R.string.there_no_such_vacancies)
            }
        }
    }

    private fun showProgress() {
        with(binding) {
            searchRecycleView.isVisible = false
            centerProgressBar.isVisible = true
            textUnderSearch.isVisible = false
            searchDefaultPlaceholder.isVisible = false
            noVacancyToShow.isVisible = false
            noConnectionPlaceholder.isVisible = false
            binding.badRequest.isVisible = false
            binding.badRequestText.isVisible = false
        }
    }

    private fun showBlank(showDefaultPlaceholder: Boolean = true) {
        with(binding) {
            searchRecycleView.isVisible = false
            centerProgressBar.isVisible = false
            textUnderSearch.isVisible = false
            searchDefaultPlaceholder.isVisible = showDefaultPlaceholder
            noVacancyToShow.isVisible = false
            noConnectionPlaceholder.isVisible = false
            noConnectionText.isVisible = false
            noVacancyToShowText.isVisible = false
            binding.badRequest.isVisible = false
            binding.badRequestText.isVisible = false
        }
    }

    private fun actionOnClick(id: String) {
        if (!viewModel.isClickable) return
        val navController = findNavController()
        val bundle = Bundle()
        bundle.putString(BUNDLE_KEY_VACANCY_ID, id)
        navController.navigate(R.id.vacancyFragment, bundle)
        viewModel.actionOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard(binding.searchQuery)
        viewModel.checkChangeFilter()
    }
}
