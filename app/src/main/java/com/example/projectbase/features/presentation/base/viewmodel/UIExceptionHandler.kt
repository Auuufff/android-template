package com.example.projectbase.features.presentation.base.viewmodel

import com.example.projectbase.core.exception.HandledExceptions

interface UIExceptionHandler {
    fun handleException(exception: HandledExceptions)
}