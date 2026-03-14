package com.example.dicodingeventandroidsubmission.data.retrofit

import com.example.dicodingeventandroidsubmission.data.response.EventDetailResponse
import com.example.dicodingeventandroidsubmission.data.response.EventListResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEventList(
        @Query("active") active: Int
    ): Call<EventListResponse>

    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") id: String
    ): Call<EventDetailResponse>
}