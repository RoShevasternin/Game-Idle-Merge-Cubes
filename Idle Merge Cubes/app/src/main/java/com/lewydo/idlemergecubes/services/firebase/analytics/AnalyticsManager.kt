package com.lewydo.idlemergecubes.services.firebase.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class AnalyticsManager {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    // ------------------------------------------------------------------------
    // Events
    // ------------------------------------------------------------------------

    private object Event {
        const val MERGE              = "merge"
        const val BUY_CUBE           = "buy_cube"
        const val COLLECT_IDLE       = "collect_idle"
        const val COLLECT_IDLE_X2    = "collect_idle_x2"
        const val COLLECT_OFFLINE    = "collect_offline"
        const val COLLECT_OFFLINE_X2 = "collect_offline_x2"
        const val LEVEL_UP_X2        = "level_up_x2"
    }

    private object Param {
        const val CUBE_LEVEL = "cube_level"
        const val AMOUNT     = "amount"
        const val PRICE      = "price"
    }

    // ------------------------------------------------------------------------
    // Tutorial
    // ------------------------------------------------------------------------

    fun tutorialBegin() {
        analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    fun tutorialComplete() {
        analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
    }

    // ------------------------------------------------------------------------
    // Grid
    // ------------------------------------------------------------------------

    fun merge(cubeLevel: Int) {
        analytics.logEvent(Event.MERGE, Bundle().apply {
            putInt(Param.CUBE_LEVEL, cubeLevel)
        })
    }

    fun buyCube(price: Long) {
        analytics.logEvent(Event.BUY_CUBE, Bundle().apply {
            putLong(Param.PRICE, price)
        })
    }

    // ------------------------------------------------------------------------
    // Idle
    // ------------------------------------------------------------------------

    fun collectIdle(amount: Long) {
        analytics.logEvent(Event.COLLECT_IDLE, Bundle().apply {
            putLong(Param.AMOUNT, amount)
        })
    }

    fun collectIdleX2(amount: Long) {
        analytics.logEvent(Event.COLLECT_IDLE_X2, Bundle().apply {
            putLong(Param.AMOUNT, amount)
        })
    }

    fun collectOffline(amount: Long) {
        analytics.logEvent(Event.COLLECT_OFFLINE, Bundle().apply {
            putLong(Param.AMOUNT, amount)
        })
    }

    fun collectOfflineX2(amount: Long) {
        analytics.logEvent(Event.COLLECT_OFFLINE_X2, Bundle().apply {
            putLong(Param.AMOUNT, amount)
        })
    }

    // ------------------------------------------------------------------------
    // Level
    // ------------------------------------------------------------------------

    fun levelUp(level: Int) {
        analytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP, Bundle().apply {
            putInt(FirebaseAnalytics.Param.LEVEL, level)
        })
    }

    fun levelUpX2(level: Int) {
        analytics.logEvent(Event.LEVEL_UP_X2, Bundle().apply {
            putInt(FirebaseAnalytics.Param.LEVEL, level)
        })
    }
}