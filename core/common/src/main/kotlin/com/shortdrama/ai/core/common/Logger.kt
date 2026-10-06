package com.shortdrama.ai.core.common

import android.util.Log

/** Thin logging wrapper so the logging backend can be swapped (e.g. Timber, Crashlytics) in one place. */
object Logger {
    private const val TAG = "ShortFilm"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}
