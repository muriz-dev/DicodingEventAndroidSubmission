package com.example.dicodingeventandroidsubmission.di

import android.content.Context
import com.example.dicodingeventandroidsubmission.data.local.room.EventsDatabase
import com.example.dicodingeventandroidsubmission.data.remote.retrofit.ApiConfig
import com.example.dicodingeventandroidsubmission.data.repository.EventsRepository

object Injection {
    fun provideRepository(context: Context): EventsRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventsDatabase.getInstance(context)
        val dao = database.eventsDao()

        return EventsRepository.getInstance(apiService, dao)
    }
}