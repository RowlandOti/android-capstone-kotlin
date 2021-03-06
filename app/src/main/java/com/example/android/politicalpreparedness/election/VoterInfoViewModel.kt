package com.example.android.politicalpreparedness.election

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(private val repository: ElectionRepository) : ViewModel() {

    private val voterInfo = MutableLiveData<VoterInfoResponse>()
    private val electionIsSaved = MutableLiveData<Boolean>()

    private val errorMessage = MutableLiveData<Int>()

    fun getErrorMessage(): LiveData<Int> {
        return errorMessage
    }

    fun getVoterInfo(): LiveData<VoterInfoResponse> {
        return voterInfo
    }

    private val electionIdAndDivisionSet = MutableLiveData<HashMap<Int, Division>>()

    private val loadVotingLocations = SingleLiveEvent<String>()
    private val loadBallotInformation = SingleLiveEvent<String>()

    fun getLoadVotingLocation(): LiveData<String> {
        return loadVotingLocations
    }

    fun getLoadBallotInformation(): LiveData<String> {
        return loadBallotInformation
    }

    fun votingLocationsClick(url :String) {
        loadVotingLocations.postValue(url)
    }

    fun ballotInfoClick(url :String) {
        loadBallotInformation.postValue(url)
    }

    fun getIsElectionSaved(): LiveData<Boolean> {
        return electionIsSaved
    }

    fun setDataFromArgs(electionId: Int, division: Division) {
        val electionIdDivisionSet = HashMap<Int, Division>()
        electionIdDivisionSet.put(electionId, division)
        electionIdAndDivisionSet.postValue(electionIdDivisionSet)

        checkIsElectionSaved(electionId)
        fetchVoterInfo(electionId, division.state)
    }

    private fun fetchVoterInfo(electionId: Int, address: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               try {
                    val voterInfoResponse = repository.fetchVoterInfo(address, electionId)
                    if (voterInfoResponse != null) {
                        voterInfo.postValue(voterInfoResponse)
                    } else {
                        errorMessage.postValue(R.string.no_content)
                    }
                } catch (e: Exception) {
                   errorMessage.postValue(R.string.msg_network_error)
                   Log.e(VoterInfoViewModel::class.java.simpleName, e.toString())
                }
            }
        }
    }

    fun followElection() {
        voterInfo.value?.election?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val id = repository.saveElection(it)
                if (id > -1) {
                    electionIsSaved.postValue(true)
                }
            }
        }
    }

    fun unFollowElection() {
        voterInfo.value?.election?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val id =repository.deleteElection(it)
                if (id > 0) {
                    electionIsSaved.postValue(false)
                }
            }
        }
    }

    private fun checkIsElectionSaved(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSaved = repository.savedElectionExists(id)
            electionIsSaved.postValue(isSaved)
        }
    }
}