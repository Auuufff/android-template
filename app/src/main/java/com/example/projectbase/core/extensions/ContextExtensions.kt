package com.example.projectbase.core.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.example.projectbase.core.exception.HandledExceptions

fun Context.showExceptionDialog(exception: HandledExceptions) {
    val builder = AlertDialog.Builder(this)
        .setMessage(exception.getText(this))
        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }

    exception.getText(this)?.let { title ->
        builder.setTitle(title)
    }

    builder.show()
}

fun Context.showSimpleDialog(
    @StringRes resId: Int,
    @StringRes titleRes: Int? = null,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this).setMessage(resId)
    titleRes?.let { builder.setTitle(it) }
    builder.setPositiveButton(android.R.string.ok) { _, _ -> onPositive.invoke() }
        .setNegativeButton(android.R.string.cancel) { _, _ -> onNegative.invoke() }
        .show()
}

fun Context.hasPermission(permission: String): Boolean {
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}
