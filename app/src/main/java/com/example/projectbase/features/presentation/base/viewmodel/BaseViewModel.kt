package com.example.projectbase.features.presentation.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.projectbase.core.exception.HandledExceptions
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    lateinit var stateHandle: SavedStateHandle
    lateinit var navController: NavController

    private val _errorLiveData = MutableLiveData<HandledExceptions>()

    val errorLiveData: LiveData<HandledExceptions>
        get() = _errorLiveData

    val progressLiveData = MutableLiveData(false)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        handledException(HandledExceptions.from(e))
    }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + coroutineExceptionHandler

    override fun onCleared() {
        coroutineContext.cancelChildren()
        super.onCleared()
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    protected fun showProgress() {
        progressLiveData.value = true
    }

    protected fun hideProgress() {
        progressLiveData.value = false
    }

    protected fun launchWithDialog(block: suspend () -> Unit) = launch {
        showProgress()
        block()
        hideProgress()
    }

    protected fun handledException(error: HandledExceptions) = _errorLiveData.postValue(error)
}