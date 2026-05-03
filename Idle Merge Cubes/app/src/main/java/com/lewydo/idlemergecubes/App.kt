package com.lewydo.idlemergecubes

import android.app.Application
import android.content.Context
import com.lewydo.idlemergecubes.tiktok.TikTokManager
import com.lewydo.idlemergecubes.util.log
import com.tiktok.TikTokBusinessSdk

lateinit var appContext: Context private set

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        TikTokManager.initialize(this)
    }

}