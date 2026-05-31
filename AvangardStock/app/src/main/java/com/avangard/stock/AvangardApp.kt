package com.avangard.stock

import android.app.Application
import com.avangard.stock.data.database.AppDatabase

class AvangardApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: AvangardApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
