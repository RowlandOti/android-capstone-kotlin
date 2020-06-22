package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElectionsViewModel(private val repository: ElectionRepository) : ViewModel() {

    private val upcomingElections = MutableLiveData<List<Election>>()
    private val savedElections = MutableLiveData<List<Election>>()

    private val errorMessage = MutableLiveData<Int>()

    fun getErrorMessage(): LiveData<Int> {
        return errorMessage
    }

    fun getUpcomingElections(): LiveData<List<Election>> {
        return upcomingElections
    }

    fun getSavedElections(): LiveData<List<Election>> {
        return savedElections
    }

    fun fetchUpcomingElections() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val elections = repository.fetchUpcomingElections()
                upcomingElections.postValue(elections)
            } catch (e: Exception) {
                errorMessage.postValue(R.string.msg_network_error)
                Log.e(ElectionsViewModel::class.java.simpleName, e.toString())
            }
        }
    }

    fun loadSavedElections() {
        viewModelScope.launch(Dispatchers.IO) {
            val elections = repository.getSavedElections()
            savedElections.postValue(elections)
        }

    }
}