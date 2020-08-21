package com.example.projectbase.features.presentation.base.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.projectbase.R
import com.example.projectbase.databinding.DialogProgressBinding

/**
 * Dialog to prevent user communication with ui and show long background operation.
 */
class ProgressDialog : AppCompatDialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.let { d ->
            d.window?.let { w ->
                w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            d.setCanceledOnTouchOutside(false)
            d.setCancelable(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val binding = DataBindingUtil.inflate<DialogProgressBinding>(
            inflater,
            R.layout.dialog_progress,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    fun setVisibility(isVisible: Boolean, fragmentManager: FragmentManager) {
        val fragment = fragmentManager.findFragmentByTag(PROGRESS_DIALOG_TAG)
        if (fragment != null && !isVisible) {
            (fragment as ProgressDialog).dismissAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        } else if (fragment == null && isVisible) {
            show(fragmentManager, PROGRESS_DIALOG_TAG)
            fragmentManager.executePendingTransactions()
        }
    }

    companion object {
        private const val PROGRESS_DIALOG_TAG = "PROGRESS_DIALOG_TAG"

        fun newInstance() = ProgressDialog()
    }
}