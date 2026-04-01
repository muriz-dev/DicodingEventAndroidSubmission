package com.example.dicodingeventandroidsubmission.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventandroidsubmission.data.local.entity.EventsEntity
import com.example.dicodingeventandroidsubmission.data.repository.EventsRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    fun getEventDetail(eventId: Int) = eventsRepository.getEventDetail(eventId)

    fun saveToFavorite(event: EventsEntity) {
        viewModelScope.launch {
            eventsRepository.setEventsFavorite(event, true)
        }
    }

    fun deleteFromFavorite(event: EventsEntity) {
        viewModelScope.launch {
            eventsRepository.setEventsFavorite(event, false)
        }
    }
}