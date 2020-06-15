package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table ORDER BY id DESC")
    suspend fun getAll():List<Election>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table ORDER BY id DESC LIMIT 1")
    suspend fun getById(): Election

    //TODO: Add delete query
    @Query("DELETE FROM election_table")
    suspend fun deleteAll()

    //TODO: Add clear query

}