package com.dicoding.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.ui.EventAdapter
import com.dicoding.dicodingevent.databinding.FragmentHomeBinding
import com.dicoding.dicodingevent.di.Injection
import com.dicoding.dicodingevent.ui.detailEvent.DetailEventActivity
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels{
        HomeViewModelFactory(Injection.provideRepository(requireContext()))
    }
    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedEventAdapter: EventAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUpcomingRecyclerView()
        setupFinishedRecyclerView()

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) {
            upcomingAdapter.submitList(it)
        }
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) {
            finishedEventAdapter.submitList(it)
        }

        homeViewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showSnackbar(message)
            }
        }
        homeViewModel.fetchAllEvents()

        return root
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupUpcomingRecyclerView() {
        upcomingAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", event.id)
            }
            startActivity(intent)
        }
        binding.rvUpcomingEvents.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingEvents.adapter = upcomingAdapter
    }

    private fun setupFinishedRecyclerView() {
        finishedEventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", event.id)
            }
            startActivity(intent)
        }
        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedEvents.adapter = finishedEventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
