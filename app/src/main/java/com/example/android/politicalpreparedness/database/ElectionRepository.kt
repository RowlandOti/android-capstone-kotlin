package com.example.android.politicalpreparedness.database

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

/**
 * Created by rowlandoti on 11/06/2020
 * Last modified 11/06/2020
 */

class ElectionRepository(
        private val database: ElectionDatabase
) {

    private val electionDao = database.electionDao

    suspend fun getSavedElections() : List<Election> {
        return electionDao.getAll()
    }

    suspend fun fetchUpcomingElections(): List<Election> {
        val result = CivicsApi.retrofitService.getElection()
        return if (result.isSuccessful) {
            result.body()?.elections ?: emptyList()
        } else emptyList()
    }

    suspend fun fetchVoterInfo(address: String, electionId: Int): VoterInfoResponse? {
        val result = CivicsApi.retrofitService.getVoterInfo(address,electionId)
        return if (result.isSuccessful) {
            result.body()
        } else null
    }

    suspend fun savedElectionExists(id: Int) : Boolean {
        val election = electionDao.getById(id)
        return election != null
    }

    suspend fun saveElection(election :Election) : Long {
        return electionDao.insert(election)
    }

    suspend fun deleteElection(election :Election) : Int {
        return electionDao.delete(election)
    }
}
