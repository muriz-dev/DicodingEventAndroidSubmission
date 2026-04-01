package com.example.dicodingeventandroidsubmission.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingeventandroidsubmission.data.repository.EventsRepository
import com.example.dicodingeventandroidsubmission.di.Injection

class SearchViewModelFactory(private val eventsRepository: EventsRepository): ViewModelProvider.Factory {
    companion object {
        @Volatile
        private var instance: SearchViewModelFactory? = null
        fun getInstance(context: Context): SearchViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SearchViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(eventsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}