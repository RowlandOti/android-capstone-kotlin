package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(private val repository: ElectionRepository) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val voterInfo = MutableLiveData<VoterInfoResponse>()

    //TODO: Add var and methods to populate voter info
    fun getVoterInfo(): LiveData<VoterInfoResponse> {
        return voterInfo
    }

    private val electionIdAndDivisionSet = MutableLiveData<HashMap<Int, Division>>()

    private val loadVotingLocations = SingleLiveEvent<Boolean>()
    private val loadBallotInformation = SingleLiveEvent<Boolean>()

    fun getLoadVotingLocation(): LiveData<Boolean> {
        return loadVotingLocations
    }

    fun getLoadBallotInformation(): LiveData<Boolean> {
        return loadBallotInformation
    }

    fun votingLocationsClick() {
        loadVotingLocations.postValue(true)
    }

    fun ballotInfoClick() {
        loadBallotInformation.postValue(true)
    }

    fun setDataFromArgs(electionId: Int, division: Division) {
        val electionIdDivisionSet = HashMap<Int, Division>()
        electionIdDivisionSet.put(electionId, division)
        electionIdAndDivisionSet.postValue(electionIdDivisionSet)

        fetchVoterInfo(electionId, division.state)
    }

    private fun fetchVoterInfo(electionId: Int, address: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val voterInfoResponse = repository.fetchVoterInfo(address, electionId)
                voterInfo.postValue(voterInfoResponse)
            }
        }
    }

    fun followElection() {
        voterInfo.value?.election?.let {
            repository.saveElection(it)
        }
    }

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}