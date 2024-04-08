package ru.practicum.android.diploma.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavouritesBinding
import ru.practicum.android.diploma.presentation.FavouritesViewModel
import ru.practicum.android.diploma.util.adapter.FavouritesVacancyAdapter

class FavouritesFragment : Fragment() {

    private val viewModel by viewModel<FavouritesViewModel>()

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FavouritesVacancyAdapter { actionOnClick(it.id) }
        binding.rvVacancies.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .catch {
                        binding.groupError.isVisible = true
                        binding.groupEmpty.isVisible = false
                        binding.rvVacancies.isVisible = false
                    }
                    .collect {
                        binding.groupError.isVisible = false
                        if (it.isEmpty()) {
                            binding.groupEmpty.isVisible = true
                            binding.rvVacancies.isVisible = false

                        } else {
                            binding.groupEmpty.isVisible = false
                            binding.rvVacancies.isVisible = true
                            adapter.submitData(it)
                        }

                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun actionOnClick(id: String) {
        if (!viewModel.isClickable) return
        val navController = findNavController()
        val bundle = Bundle()
        bundle.putString("vacancyId", id)
        navController.navigate(R.id.vacancyFragment, bundle)
        viewModel.actionOnClick()
    }
}
