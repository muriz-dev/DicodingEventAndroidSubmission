package com.example.dicodingeventandroidsubmission.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.dicodingeventandroidsubmission.data.Result
import com.example.dicodingeventandroidsubmission.data.local.entity.EventsEntity
import com.example.dicodingeventandroidsubmission.data.local.room.EventsDao
import com.example.dicodingeventandroidsubmission.data.remote.response.ListEventsItem
import com.example.dicodingeventandroidsubmission.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers

class EventsRepository private constructor(
    private val apiService: ApiService,
    private val eventsDao: EventsDao,
) {
    companion object {
        @Volatile
        private var instance: EventsRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventsDao: EventsDao
        ): EventsRepository =
            instance ?: synchronized(this) {
                instance ?: EventsRepository(apiService, eventsDao)
            }.also { instance = it }
    }

    fun getEvents(activeStatus: Int): LiveData<Result<List<EventsEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        val isUpcomingStatus = (activeStatus == 1)

        val currentLocalData = if (isUpcomingStatus) {
            eventsDao.getUpcomingEventsSync()
        } else {
            eventsDao.getFinishedEventsSync()
        }

        if (currentLocalData.isNotEmpty()) {
            emit(Result.Success(currentLocalData))
        }

        try {
            val response = apiService.getEventList(activeStatus)
            val events = response.listEvents

            val eventsList = events.map { event ->
                val isFavorite = eventsDao.isEventsFavorite(event.id)
                EventsEntity(
                    event.id,
                    event.name,
                    event.ownerName,
                    event.cityName,
                    event.beginTime,
                    event.endTime,
                    event.quota,
                    event.registrants,
                    event.description,
                    event.mediaCover,
                    event.link,
                    isUpcomingStatus,
                    isFavorite
                )
            }

            eventsDao.deleteNonFavoriteEventsByStatus(isUpcomingStatus)
            eventsDao.insertEvents(eventsList)
        } catch (e: Exception) {
            Log.e("EventsRepository", "getEvents: ${e.message.toString()} ")

            if (currentLocalData.isEmpty()) {
                emit(Result.Error("Tidak ada koneksi internet. Silakan coba lagi nanti."))
            }
        }

        val localData: LiveData<Result<List<EventsEntity>>> = if (activeStatus == 1) {
            eventsDao.getUpcomingEvents().map { Result.Success(it) }
        } else {
            eventsDao.getFinishedEvents().map { Result.Success(it) }
        }

        emitSource(localData)
    }

    fun getRandomSuggestions(): LiveData<Result<List<EventsEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        val localData: LiveData<Result<List<EventsEntity>>> = eventsDao.getRandomSuggestions().map { Result.Success(it) }
        emitSource(localData)
    }

    fun searchEvents(query: String): LiveData<Result<List<EventsEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            val response = apiService.getEventSearch(-1, query)
            val events = response.listEvents

            val eventsList = events.map { event ->
                val isUpcomingStatus = eventsDao.isEventsUpcoming(event.id)
                val isFavorite = eventsDao.isEventsFavorite(event.id)
                EventsEntity(
                    event.id,
                    event.name,
                    event.ownerName,
                    event.cityName,
                    event.beginTime,
                    event.endTime,
                    event.quota,
                    event.registrants,
                    event.description,
                    event.mediaCover,
                    event.link,
                    isUpcomingStatus,
                    isFavorite
                )
            }

            eventsDao.insertEvents(eventsList)
        } catch (e: Exception) {
            Log.e("EventsRepository", "searchEvents: ${e.message}")
        }

        val localData: LiveData<Result<List<EventsEntity>>> = eventsDao.getEventByName(query).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getEventDetail(id: Int): LiveData<Result<EventsEntity>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        val cachedEvent = eventsDao.getEventByIdSync(id)
        if (cachedEvent != null) {
            emit(Result.Success(cachedEvent))
        }

        try {
            val response = apiService.getEventDetail(id.toString())
            val detail = response.event
            val isFavorite = eventsDao.isEventsFavorite(detail.id)
            val currentIsUpcoming = cachedEvent?.isUpcoming ?: false

            val eventDetail = EventsEntity(
                detail.id, detail.name, detail.ownerName, detail.cityName,
                detail.beginTime, detail.endTime, detail.quota, detail.registrants,
                detail.description, detail.mediaCover, detail.link,
                currentIsUpcoming, isFavorite
            )

            eventsDao.updateEvent(eventDetail)
        } catch (e: Exception) {
            Log.e("EventsRepository", "Detail Fetch Failed: ${e.message}")

            if (cachedEvent == null) {
                emit(Result.Error("Gagal memuat detail event. Periksa koneksi Anda."))
            }
        }

        val localData: LiveData<Result<EventsEntity>> = eventsDao.getEventById(id).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getFavoriteEvents(): LiveData<Result<List<EventsEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        val localData: LiveData<Result<List<EventsEntity>>> = eventsDao.getFavoriteEvents().map { Result.Success(it) }
        emitSource(localData)
    }

    suspend fun setEventsFavorite(event: EventsEntity, favoriteState: Boolean) {
        event.isFavorite = favoriteState
        eventsDao.updateEvent(event)
    }

    suspend fun getDailyReminderEvent(): ListEventsItem? {
        return try {
            val response = apiService.getOneActiveEvent()
            val listEvents = response.listEvents

            if (listEvents.isNotEmpty()) {
                listEvents[0]
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "getDailyReminderEvent: ${e.message}")
            null
        }
    }

}