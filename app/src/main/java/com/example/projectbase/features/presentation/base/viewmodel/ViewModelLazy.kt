package com.example.projectbase.features.presentation.base.viewmodel

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.getStateHandle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.example.projectbase.core.di.Injector
import com.example.projectbase.core.extensions.listen
import com.example.projectbase.features.presentation.base.activity.BaseActivity
import com.example.projectbase.features.presentation.base.fragment.BaseFragment
import kotlin.reflect.KClass

@MainThread
inline fun <reified VM : BaseViewModel> BaseActivity<*>.viewModelLazy(@IdRes navHostFragmentId: Int): Lazy<VM> =
    ActivityViewModelLazy({ this }, VM::class, navHostFragmentId)

@MainThread
inline fun <reified VM : BaseViewModel> BaseFragment.viewModelLazy(): Lazy<VM> =
    FragmentViewModelLazy({ this }, VM::class)

@MainThread
inline fun <reified VM : BaseViewModel> BaseFragment.navGraphViewModelLazy(@IdRes navGraphId: Int): Lazy<VM> =
    FragmentNavGraphViewModelLazy({ this }, VM::class, navGraphId)

open class FragmentViewModelLazy<VM : BaseViewModel>(
    private val fragmentProducer: () -> BaseFragment,
    viewModelClass: KClass<VM>
) : BaseViewModelLazy<VM>(viewModelClass) {

    override val storeProducer: () -> ViewModelStore
        get() = { fragmentProducer().viewModelStore }

    override val defaultArgsProducer: () -> Bundle?
        get() = { fragmentProducer().arguments }

    override val ownerProducer: () -> SavedStateRegistryOwner
        get() = { fragmentProducer() }

    override val exceptionHandlerProducer: () -> UIExceptionHandler
        get() = { fragmentProducer() }

    override val navControllerProducer: () -> NavController
        get() = { fragmentProducer().findNavController() }

    override val progressController: () -> ProgressController
        get() = { fragmentProducer() }
}

class ActivityViewModelLazy<VM : BaseViewModel>(
    private val activityProducer: () -> BaseActivity<*>,
    viewModelClass: KClass<VM>,
    @IdRes val navHostFragmentId: Int
) : BaseViewModelLazy<VM>(viewModelClass) {

    override val storeProducer: () -> ViewModelStore
        get() = { activityProducer().viewModelStore }

    override val defaultArgsProducer: () -> Bundle?
        get() = { activityProducer().intent.extras }

    override val ownerProducer: () -> SavedStateRegistryOwner
        get() = { activityProducer() }

    override val exceptionHandlerProducer: () -> UIExceptionHandler
        get() = { activityProducer() }

    override val navControllerProducer: () -> NavController
        get() = { activityProducer().findNavController(navHostFragmentId) }

    override val progressController: () -> ProgressController
        get() = { activityProducer() }
}

class FragmentNavGraphViewModelLazy<VM : BaseViewModel>(
    fragmentProducer: () -> BaseFragment,
    viewModelClass: KClass<VM>,
    @IdRes private val navGraphId: Int
) : FragmentViewModelLazy<VM>(fragmentProducer, viewModelClass) {
    override val storeProducer: () -> ViewModelStore
        get() = {
            navControllerProducer().getBackStackEntry(navGraphId).viewModelStore
        }
}

abstract class BaseViewModelLazy<VM : BaseViewModel>(private val viewModelClass: KClass<VM>) :
    Lazy<VM> {

    private var cached: VM? = null

    abstract val storeProducer: () -> ViewModelStore

    abstract val defaultArgsProducer: () -> Bundle?

    abstract val ownerProducer: () -> SavedStateRegistryOwner

    abstract val exceptionHandlerProducer: () -> UIExceptionHandler

    abstract val navControllerProducer: () -> NavController

    abstract val progressController: () -> ProgressController

    protected open val factoryProducer: () -> ViewModelProvider.Factory =
        { Injector.viewModelFactory() }

    override val value: VM
        get() {
            return cached ?: buildViewModel(storeProducer(), factoryProducer())
        }

    protected open fun buildViewModel(
        store: ViewModelStore,
        factory: ViewModelProvider.Factory
    ): VM {
        return ViewModelProvider(store, factory).get(viewModelClass.java).also {
            it.navController = navControllerProducer()
            cached = it
            val defaultArgs = defaultArgsProducer()
            val owner = ownerProducer()
            it.stateHandle = getStateHandle(
                owner.savedStateRegistry,
                owner.lifecycle,
                it::class.java.name,
                defaultArgs
            )
            it.errorLiveData.listen(owner) { ex ->
                exceptionHandlerProducer().handleException(ex)
            }
            it.progressLiveData.listen(owner) {
                if (it) progressController().showProgress()
                else progressController().hideProgress()
            }
        }
    }

    override fun isInitialized() = cached != null
}