package com.example.projectbase.core.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Removes all previously registered observers for current lifecycle owner and live data
 * Callback returns null values if it was passed to live data
 */
fun <T> LiveData<T>.listen(lifecycleOwner: LifecycleOwner, observer: (T) -> (Unit)) {
    removeObservers(lifecycleOwner)
    observe(lifecycleOwner, Observer {
        observer.invoke(it)
    })
}

/**
 * Removes all previously registered observers for current lifecycle owner and live data
 * Callback do not returns null values if it was passed to live data
 */
fun <T> LiveData<T>.listenNonNull(lifecycleOwner: LifecycleOwner, observer: (T) -> (Unit)) {
    removeObservers(lifecycleOwner)
    observe(lifecycleOwner, Observer {
        if (it != null) {
            observer.invoke(it)
        }
    })
}