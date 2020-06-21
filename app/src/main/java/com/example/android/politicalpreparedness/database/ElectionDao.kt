package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg election: Election): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election):Long

    @Query("SELECT * FROM election_table ORDER BY id DESC")
    suspend fun getAll():List<Election>

    @Query("SELECT * FROM election_table WHERE id =:id")
    suspend fun getById(id: Int): Election?

    @Query("DELETE FROM election_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(election: Election): Int
}