package com.example.dicodingeventandroidsubmission.ui.common

import androidx.lifecycle.ViewModel
import com.example.dicodingeventandroidsubmission.data.repository.EventsRepository

class EventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    fun getEvents(activeStatus: Int) = eventsRepository.getEvents(activeStatus)

    fun getFavoriteEvents() = eventsRepository.getFavoriteEvents()
}