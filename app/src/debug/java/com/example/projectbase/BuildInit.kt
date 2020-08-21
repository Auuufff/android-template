package com.example.projectbase

import android.app.Application
import timber.log.Timber

fun initBuildVariant(app: Application) {
    Timber.plant(Timber.DebugTree())
}