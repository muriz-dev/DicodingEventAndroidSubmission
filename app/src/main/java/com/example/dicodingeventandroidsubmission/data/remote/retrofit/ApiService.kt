package com.example.dicodingeventandroidsubmission.data.remote.retrofit

import com.example.dicodingeventandroidsubmission.data.remote.response.EventDetailResponse
import com.example.dicodingeventandroidsubmission.data.remote.response.EventListResponse
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEventList(
        @Query("active") active: Int
    ): EventListResponse

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: String
    ): EventDetailResponse

    @GET("events")
    suspend fun getEventSearch(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): EventListResponse
}