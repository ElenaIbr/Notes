package com.example.notes.app

import android.app.Application
import com.example.notes.database.DbManager

class MyApplication : Application() {

    lateinit var myDbManager : DbManager

    override fun onCreate() {
        super.onCreate()

        myDbManager = DbManager(this)
        myDbManager.openDb()
    }
}