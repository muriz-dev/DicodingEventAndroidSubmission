package com.example.dicodingeventandroidsubmission.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.dicodingeventandroidsubmission.data.repository.EventsRepository

class SearchViewModel(private val eventsRepository: EventsRepository): ViewModel() {
    private val _query = MutableLiveData<String>()

    val searchResult = _query.switchMap { query ->
        if (query.isNullOrEmpty()) {
            eventsRepository.getRandomSuggestions()
        } else {
            eventsRepository.searchEvents(query)
        }
    }

    fun setSearchQuery(query: String) {
        if (_query.value == query) return
        _query.value = query
    }

    fun getInitialSuggestions() {
        if (_query.value == null) {
            _query.value = ""
        }
    }
}