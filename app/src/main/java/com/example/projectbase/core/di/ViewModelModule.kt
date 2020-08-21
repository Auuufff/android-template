package com.example.projectbase.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectbase.features.presentation.base.viewmodel.BaseViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Class for mapping view models
 * Each method should have an unique name
 */
@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    //todo remove when at least one ViewModel will be added to the map
    @Module
    companion object {
        @Provides
        @JvmStatic
        @IntoMap
        @ViewModelKey(BaseViewModel::class)
        fun provideDummyViewModel(): ViewModel = BaseViewModel()
    }
}