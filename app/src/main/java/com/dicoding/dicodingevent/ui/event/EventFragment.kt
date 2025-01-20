package com.dicoding.dicodingevent.ui.event

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.ui.EventAdapter
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.databinding.FragmentEventBinding
import com.dicoding.dicodingevent.di.Injection
import com.dicoding.dicodingevent.ui.detailEvent.DetailEventActivity
import com.google.android.material.snackbar.Snackbar

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(
            Injection.provideRepository(requireContext()),
            arguments?.getInt("eventType", 1) ?: 1
        )
    }
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()
        setupSearchView()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchEvents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchEvents(it) }
                return true
            }
        })
    }

    private fun searchEvents(keyword: String) {
        eventViewModel.searchEvents(keyword).observe(viewLifecycleOwner){ result ->
            when (result){
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    eventAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    showSnackbar("Error: ${result.error}")
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe loading status
        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Observe events data
        eventViewModel.events.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    eventAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    showSnackbar("Error: ${result.error}")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", event.id)
            }
            startActivity(intent)
        }
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = eventAdapter
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
