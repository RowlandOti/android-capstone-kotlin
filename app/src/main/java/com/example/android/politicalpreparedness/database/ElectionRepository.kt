package com.example.android.politicalpreparedness.database

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election

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
}
