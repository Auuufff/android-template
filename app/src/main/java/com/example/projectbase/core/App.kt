package com.example.projectbase.core

import android.app.Application
import com.example.projectbase.core.di.DaggerAppComponent
import com.example.projectbase.core.di.Injector
import com.example.projectbase.initBuildVariant

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initBuildVariant(this)

        Injector.component = DaggerAppComponent.builder()
            .context(this)
            .build()
    }
}