package com.example.projectbase.features.presentation.base.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.projectbase.core.exception.HandledExceptions
import com.example.projectbase.core.extensions.showExceptionDialog
import com.example.projectbase.features.presentation.base.fragment.ProgressDialog
import com.example.projectbase.features.presentation.base.viewmodel.ProgressController
import com.example.projectbase.features.presentation.base.viewmodel.UIExceptionHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(), UIExceptionHandler,
    CoroutineScope,
    ProgressController {
    protected val navController by lazy {
        (supportFragmentManager.findFragmentById(navHostId) as NavHostFragment).navController
    }
    protected lateinit var binding: Binding

    private val progressDialog: ProgressDialog by lazy { ProgressDialog.newInstance() }

    abstract val layoutResId: Int
    abstract val navHostId: Int
    abstract val graphId: Int

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        handleException(HandledExceptions.from(e))
    }

    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext + coroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindContentView(layoutResId)
        navController.setGraph(graphId, getStartDestinationArgs())
    }

    protected open fun getStartDestinationArgs(): Bundle {
        return intent.extras ?: Bundle.EMPTY
    }

    /**
     * Binds layout to [ViewDataBinding]
     */
    private fun <T : ViewDataBinding> bindContentView(@LayoutRes layoutRes: Int): T =
        DataBindingUtil.setContentView(this, layoutRes)

    override fun handleException(exception: HandledExceptions) {
        showExceptionDialog(exception)
    }

    override fun showProgress() {
        progressDialog.setVisibility(true, supportFragmentManager)
    }

    override fun hideProgress() {
        progressDialog.setVisibility(false, supportFragmentManager)
    }
}