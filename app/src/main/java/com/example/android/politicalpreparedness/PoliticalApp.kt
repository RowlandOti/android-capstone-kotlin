package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.database.ElectionDatabase

/**
 * Created by rowlandoti on 12/06/2020
 * Last modified 12/06/2020
 */
class PoliticalApp : Application() {

    internal lateinit var database: ElectionDatabase

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        database = ElectionDatabase.getInstance(this)
    }

    companion object {
        lateinit var INSTANCE: PoliticalApp
    }
}