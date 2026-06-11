package com.lewydo.idlemergecubes

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.lewydo.idlemergecubes.services.tiktok.TikTokManager

lateinit var appContext: Context private set

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        //TikTokManager.initialize(this)
    }

}