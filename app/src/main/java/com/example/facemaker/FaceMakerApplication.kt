package com.example.facemaker

import android.app.Application
import timber.log.Timber

class FaceMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}