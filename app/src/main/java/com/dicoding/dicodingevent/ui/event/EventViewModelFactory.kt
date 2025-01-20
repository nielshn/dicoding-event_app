package com.dicoding.dicodingevent.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.dicodingevent.data.repository.EventRepository

@Suppress("UNCHECKED_CAST")
class EventViewModelFactory(
    private val repository: EventRepository,
    private val eventType: Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(repository, eventType) as T
    }
}