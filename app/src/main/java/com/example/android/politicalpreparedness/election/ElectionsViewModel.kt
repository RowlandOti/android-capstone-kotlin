package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ElectionsViewModel(private val repository: ElectionRepository) : ViewModel() {

    private val upcomingElections = MutableLiveData<List<Election>>()
    private val savedElections = MutableLiveData<List<Election>>()

    fun getUpcomingElections(): LiveData<List<Election>> {
        return upcomingElections
    }

    fun getSavedElections(): LiveData<List<Election>> {
        return savedElections
    }

    fun fetchUpcomingElections() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val elections = repository.fetchUpcomingElections()
                upcomingElections.postValue(elections)
            }
        }
    }

    fun loadSavedElections() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val elections = repository.getSavedElections()
                savedElections.postValue(elections)
            }
        }

    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}