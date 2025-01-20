package com.dicoding.dicodingevent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.repository.EventRepository
import com.dicoding.dicodingevent.utils.Event
import kotlinx.coroutines.launch

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<EventEntity>>()
    val upcomingEvents: LiveData<List<EventEntity>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<EventEntity>>()
    val finishedEvents: LiveData<List<EventEntity>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun fetchAllEvents() {
        _isLoading.value = true

        viewModelScope.launch {
            val (upcomingResult, finishedResult) = eventRepository.fetchAllEvents()

            // Handle upcoming events result
            if (upcomingResult is Result.Success) {
                _upcomingEvents.value = upcomingResult.data.take(5)
            } else if (upcomingResult is Result.Error) {
                _snackbarText.value = Event("Failed to fetch upcoming events: ${upcomingResult.error}")
            }

            // Handle finished events result
            if (finishedResult is Result.Success) {
                _finishedEvents.value = finishedResult.data.take(5)
            } else if (finishedResult is Result.Error) {
                _snackbarText.value = Event("Failed to fetch finished events: ${finishedResult.error}")
            }

            _isLoading.value = false
        }
    }
}
