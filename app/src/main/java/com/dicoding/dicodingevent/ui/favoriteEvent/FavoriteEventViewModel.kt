package com.dicoding.dicodingevent.ui.favoriteEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.launch

class FavoriteEventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    val favoriteEvents: LiveData<List<EventEntity>> = eventRepository.getFavoriteEvents()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading get() = _isLoading

    fun saveEvent(event: EventEntity){
        viewModelScope.launch {
            eventRepository.setEventFavorite(event, true)
        }
    }

    fun deleteEvent(event: EventEntity){
        viewModelScope.launch {
            eventRepository.setEventFavorite(event, false)
        }
    }
}