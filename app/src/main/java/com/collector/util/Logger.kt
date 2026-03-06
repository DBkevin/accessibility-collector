package com.collector.util

import android.util.Log

/**
 * 日志工具
 */
object Logger {

    private const val DEFAULT_TAG = "Collector"

    fun d(message: String, tag: String = DEFAULT_TAG) {
        Log.d(tag, message)
    }

    fun i(message: String, tag: String = DEFAULT_TAG) {
        Log.i(tag, message)
    }

    fun w(message: String, tag: String = DEFAULT_TAG) {
        Log.w(tag, message)
    }

    fun e(message: String, tag: String = DEFAULT_TAG) {
        Log.e(tag, message)
    }

    fun e(message: String, throwable: Throwable, tag: String = DEFAULT_TAG) {
        Log.e(tag, message, throwable)
    }

    fun v(message: String, tag: String = DEFAULT_TAG) {
        Log.v(tag, message)
    }
}
