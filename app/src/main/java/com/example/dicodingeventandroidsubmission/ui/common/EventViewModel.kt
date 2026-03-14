package com.example.dicodingeventandroidsubmission.ui.common

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

class EventViewModel(private val active: Int) : ViewModel() {
    companion object {
        private const val TAG = "EventViewModel"
    }

    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getEventList(active)
    }

    private fun getEventList(activeStatus: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEventList(activeStatus)

        client.enqueue(object : Callback<EventListResponse> {
            override fun onResponse(
                call: Call<EventListResponse>,
                response: Response<EventListResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventList.value = response.body()?.listEvents

                    Log.d(TAG, "onResponse: ${response.body()?.listEvents?.count()}")
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventListResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
}