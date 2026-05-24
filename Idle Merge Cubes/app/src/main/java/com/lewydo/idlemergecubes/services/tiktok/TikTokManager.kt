package com.lewydo.idlemergecubes.services.tiktok

import android.app.Application
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.util.log
import com.tiktok.TikTokBusinessSdk
import com.tiktok.appevents.base.EventName

object TikTokManager {

    fun initialize(app: Application) {
        val config = TikTokBusinessSdk.TTConfig(app, BuildConfig.TIKTOK_APP_SECRET)
            .setAppId(BuildConfig.APPLICATION_ID)
            .setTTAppId(BuildConfig.TIKTOK_APP_ID)
            .setLogLevel(
                if (BuildConfig.DEBUG) TikTokBusinessSdk.LogLevel.DEBUG
                else TikTokBusinessSdk.LogLevel.NONE
            )
            .apply { if (BuildConfig.DEBUG) openDebugMode() }

        TikTokBusinessSdk.initializeSdk(config, object : TikTokBusinessSdk.TTInitCallback {
            override fun success() { log("TikTok SDK initialized SUCCESSFULLY") }
            override fun fail(code: Int, msg: String) { log("TikTok SDK initialized FAILED: $code $msg") }
        })
    }

    fun tutorialComplete()  { TikTokBusinessSdk.trackTTEvent(EventName.COMPLETE_TUTORIAL) }
    fun levelUp()           { TikTokBusinessSdk.trackTTEvent(EventName.ACHIEVE_LEVEL) }
    fun unlockAchievement() { TikTokBusinessSdk.trackTTEvent(EventName.UNLOCK_ACHIEVEMENT) }
    fun spendCredits()      { TikTokBusinessSdk.trackTTEvent(EventName.SPEND_CREDITS) }
}