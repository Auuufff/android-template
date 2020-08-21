package com.example.projectbase.core.exception

import android.content.Context
import com.example.projectbase.R
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class HandledExceptions constructor(
    override val message: String?,
    override val cause: Throwable? = null,
    private val stringResource: Int?
) : Exception() {

    open fun getText(context: Context): String =
        stringResource?.let(context::getString) ?: message ?: ""

    companion object {

        fun from(ex: Throwable) = when (ex) {
            is HandledExceptions -> ex
            is SocketTimeoutException, is UnknownHostException, is ConnectException, is SocketException -> NoConnectionException()
            else -> GenericException(ex.message.orEmpty())
        }
    }

    class NoConnectionException(cause: Throwable? = null) :
        HandledExceptions(
            message = null,
            stringResource = R.string.error_no_connection,
            cause = cause
        )

    class GenericException(message: String, cause: Throwable? = null) :
        HandledExceptions(message = message, cause = cause, stringResource = null)
}