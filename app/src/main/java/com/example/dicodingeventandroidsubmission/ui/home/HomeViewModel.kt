package com.example.dicodingeventandroidsubmission.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventandroidsubmission.data.response.EventListResponse
import com.example.dicodingeventandroidsubmission.data.response.ListEventsItem
import com.example.dicodingeventandroidsubmission.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var isUpcomingRunning = false
    private var isFinishedRunning = false

    init {
        loadAllEvents()
    }

    private fun loadAllEvents() {
        getUpcomingEvents()
        getFinishedEvents()
    }

    private fun getUpcomingEvents() {
        isUpcomingRunning = true
        _isLoading.value = true

        val client = ApiConfig.getApiService().getEventList(1)
        client.enqueue(object : Callback<EventListResponse> {
            override fun onResponse(call: Call<EventListResponse>, response: Response<EventListResponse>) {
                isUpcomingRunning = false
                checkLoadingStatus()

                if (response.isSuccessful) {
                    _upcomingEvents.value = response.body()?.listEvents
                }
            }

            override fun onFailure(call: Call<EventListResponse>, t: Throwable) {
                isUpcomingRunning = false
                checkLoadingStatus()
                Log.e(TAG, "onFailure Upcoming: ${t.message}")
            }
        })
    }

    private fun getFinishedEvents() {
        isFinishedRunning = true
        _isLoading.value = true

        val client = ApiConfig.getApiService().getEventList(0)
        client.enqueue(object : Callback<EventListResponse> {
            override fun onResponse(call: Call<EventListResponse>, response: Response<EventListResponse>) {
                isFinishedRunning = false
                checkLoadingStatus()

                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents
                }
            }

            override fun onFailure(call: Call<EventListResponse>, t: Throwable) {
                isFinishedRunning = false
                checkLoadingStatus()
                Log.e(TAG, "onFailure Finished: ${t.message}")
            }
        })
    }

    private fun checkLoadingStatus() {
        _isLoading.value = isUpcomingRunning || isFinishedRunning
    }
}