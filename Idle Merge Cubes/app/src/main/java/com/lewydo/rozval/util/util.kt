package com.lewydo.rozval.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

val Any.currentClassName: String get() = this::class.java.simpleName

fun log(message: String) {
    Log.i("ROZVAL_SOW", message)
}

fun cancelCoroutinesAll(vararg coroutine: CoroutineScope?) {
    coroutine.forEach { it?.cancel() }
}