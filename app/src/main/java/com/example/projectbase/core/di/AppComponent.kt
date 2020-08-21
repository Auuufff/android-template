package com.example.projectbase.core.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, RepositoryModule::class, MapperModule::class])
interface AppComponent {

    fun viewModelFactory(): ViewModelFactory

    @Component.Builder
    abstract class Builder {

        fun context(context: Context): Builder {
            seedContext(context)
            return this
        }

        @BindsInstance
        abstract fun seedContext(context: Context)

        abstract fun build(): AppComponent
    }
}