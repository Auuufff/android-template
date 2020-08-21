package androidx.lifecycle

import android.os.Bundle
import androidx.savedstate.SavedStateRegistry

fun getStateHandle(registry: SavedStateRegistry, lifecycle: Lifecycle, key: String, defaultArgs: Bundle?): SavedStateHandle {
    val controller = SavedStateHandleController.create(registry, lifecycle, key, defaultArgs)
    return controller.handle
}