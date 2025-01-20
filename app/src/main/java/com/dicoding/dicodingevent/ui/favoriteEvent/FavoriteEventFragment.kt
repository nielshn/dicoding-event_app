package com.dicoding.dicodingevent.ui.favoriteEvent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.databinding.FragmentFavoriteEventBinding
import com.dicoding.dicodingevent.di.Injection
import com.dicoding.dicodingevent.ui.EventAdapter
import com.dicoding.dicodingevent.ui.detailEvent.DetailEventActivity

class FavoriteEventFragment : Fragment() {
    private var _binding: FragmentFavoriteEventBinding? = null
    private val binding get() = _binding

    private lateinit var eventAdapter: EventAdapter
    private val favoriteViewModel: FavoriteEventViewModel by viewModels {
        FavoriteEventViewModelFactory(Injection.provideRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteEventBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        return binding?.root
    }

    private fun observeViewModel() {
        favoriteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        favoriteViewModel.favoriteEvents.observe(viewLifecycleOwner) { favoritedEvents ->
            showLoading(false)
            eventAdapter.submitList(favoritedEvents)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            if (event.isFavorited) {
                favoriteViewModel.saveEvent(event)
            } else {
                favoriteViewModel.deleteEvent(event)
            }
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", event.id)
                putExtra("IS_FAVORITE", event.isFavorited)
            }
            startActivity(intent)
        }
        binding?.rvFavoriteEvent?.layoutManager = LinearLayoutManager(requireContext())
        binding?.rvFavoriteEvent?.adapter = eventAdapter
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
