package com.example.dicodingeventandroidsubmission.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventandroidsubmission.data.response.Event
import com.example.dicodingeventandroidsubmission.data.response.EventDetailResponse
import com.example.dicodingeventandroidsubmission.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _eventDetail = MutableLiveData<Event>()
    val eventDetail: LiveData<Event> = _eventDetail
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getEventDetail(id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEventDetail(id)
        client.enqueue(object : Callback<EventDetailResponse> {
            override fun onResponse(call: Call<EventDetailResponse>, response: Response<EventDetailResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()?.event

                    Log.d(TAG, "onResponse: ${response.body()?.event}")
                } else {
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<EventDetailResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message

                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
}