package com.dicoding.dicodingevent.ui.detailEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.local.entity.DetailEventEntity
import com.dicoding.dicodingevent.data.local.entity.EventEntity
import com.dicoding.dicodingevent.data.repository.EventRepository
import kotlinx.coroutines.launch

class DetailEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<Result<DetailEventEntity>>()
    val eventDetail: LiveData<Result<DetailEventEntity>> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getEventDetail(eventId)) {
                is Result.Success -> {
                    _eventDetail.value = result
                    checkIfFavorite(eventId.toInt())
                    _isLoading.value = false
                }

                is Result.Error -> {
                    _eventDetail.value = result
                    _isLoading.value = false
                }

                else -> {
                    _eventDetail.value = Result.Error("Unexpected error occurred")
                    _isLoading.value = false
                }
            }
        }
    }

    private fun checkIfFavorite(eventId: Int) {
        viewModelScope.launch {
            repository.isFavoriteEventLiveData(eventId).observeForever { isFavored ->
                _isFavorite.value = isFavored
            }
        }
    }


    private fun mapToEventEntity(detail: DetailEventEntity): EventEntity {
        return EventEntity(
            id = detail.id,
            name = detail.name,
            beginTime = detail.beginTime,
            isFavorited = true,
            mediaCover = detail.mediaCover,
            link = detail.link,
        )
    }

    fun toggleFavorite(detail: DetailEventEntity) {
        viewModelScope.launch {
            val event = mapToEventEntity(detail)
            val isCurrentlyFavorite = _isFavorite.value == true

            if (isCurrentlyFavorite) {
                repository.deleteFavoriteEvent(event.id)
            } else {
                repository.insertFavoriteEvent(listOf(event))
            }
        }
    }
}