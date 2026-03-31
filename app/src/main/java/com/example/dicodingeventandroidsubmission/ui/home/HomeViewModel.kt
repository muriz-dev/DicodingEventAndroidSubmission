package com.example.dicodingeventandroidsubmission.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventandroidsubmission.data.remote.response.EventListResponse
import com.example.dicodingeventandroidsubmission.data.remote.response.ListEventsItem
import com.example.dicodingeventandroidsubmission.data.remote.retrofit.ApiConfig
import com.example.dicodingeventandroidsubmission.data.repository.EventsRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    fun getUpcomingEvents() = eventsRepository.getEvents(1)

    fun getFinishedEvents() = eventsRepository.getEvents(0)
}