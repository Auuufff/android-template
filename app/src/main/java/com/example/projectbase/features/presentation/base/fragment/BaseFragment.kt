package com.example.projectbase.features.presentation.base.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.projectbase.core.exception.HandledExceptions
import com.example.projectbase.core.extensions.showExceptionDialog
import com.example.projectbase.features.presentation.base.viewmodel.ProgressController
import com.example.projectbase.features.presentation.base.viewmodel.UIExceptionHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope

open class BaseFragment : Fragment(), UIExceptionHandler, CoroutineScope, ProgressController {

    private val progressDialog: ProgressDialog by lazy { ProgressDialog.newInstance() }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        handleException(HandledExceptions.from(e))
    }

    override val coroutineContext = lifecycleScope.coroutineContext + coroutineExceptionHandler

    override fun showProgress() {
        progressDialog.setVisibility(true, childFragmentManager)
    }

    override fun hideProgress() {
        progressDialog.setVisibility(false, childFragmentManager)
    }

    override fun handleException(exception: HandledExceptions) {
        requireContext().showExceptionDialog(exception)
        hideProgress()
    }
}