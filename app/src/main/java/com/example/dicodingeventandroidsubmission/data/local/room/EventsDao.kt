package com.example.dicodingeventandroidsubmission.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingeventandroidsubmission.data.local.entity.EventsEntity

@Dao
interface EventsDao {
    @Query("SELECT * FROM Events WHERE isUpcoming = 1 ORDER BY beginTime DESC")
    fun getUpcomingEvents(): LiveData<List<EventsEntity>>

    @Query("SELECT * FROM events WHERE isUpcoming = 1 ORDER BY beginTime DESC")
    fun getUpcomingEventsSync(): List<EventsEntity>

    @Query("SELECT * FROM Events WHERE isUpcoming = 0 ORDER BY beginTime DESC")
    fun getFinishedEvents(): LiveData<List<EventsEntity>>

    @Query("SELECT * FROM events WHERE isUpcoming = 0 ORDER BY beginTime DESC")
    fun getFinishedEventsSync(): List<EventsEntity>

    @Query("SELECT * FROM Events WHERE id = :id")
    fun getEventById(id: Int): LiveData<EventsEntity>

    @Query("SELECT * FROM Events WHERE id = :id")
    suspend fun getEventByIdSync(id: Int): EventsEntity?

    @Query("SELECT * FROM Events WHERE isFavorite = 1 ORDER BY beginTime DESC")
    fun getFavoriteEvents(): LiveData<List<EventsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventsEntity>)

    @Update
    suspend fun updateEvent(event: EventsEntity)

    @Query("DELETE FROM Events WHERE isFavorite = 0")
    suspend fun deleteNonFavoriteEvents()

    @Query("DELETE FROM Events WHERE isFavorite = 0 AND isUpcoming = :isUpcoming")
    suspend fun deleteNonFavoriteEventsByStatus(isUpcoming: Boolean)

    @Query("SELECT EXISTS(SELECT * FROM Events WHERE id = :id AND isFavorite = 1)")
    suspend fun isEventsFavorite(id: Int): Boolean
}