package com.example.projectbase.features.presentation.base.adapter

import com.example.projectbase.features.presentation.base.adapter.event.ViewEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

/**
 * Base interface for any Adapter
 */
interface Adapter : CoroutineScope {
    fun listenToClicks(): Channel<ViewEvents.ClickEvent>
    fun listenToErrors(): Channel<Throwable>
}