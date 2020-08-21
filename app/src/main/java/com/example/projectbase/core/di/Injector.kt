package com.example.projectbase.core.di

object Injector : AppComponent {

    lateinit var component: AppComponent

    override fun viewModelFactory() = component.viewModelFactory()
}