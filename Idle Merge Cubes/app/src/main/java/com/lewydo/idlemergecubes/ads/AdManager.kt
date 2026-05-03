package com.lewydo.idlemergecubes.ads

import com.google.android.gms.ads.MobileAds
import com.lewydo.idlemergecubes.MainActivity
import com.lewydo.idlemergecubes.databinding.ActivityMainBinding
import com.lewydo.idlemergecubes.util.log

class AdManager(
    private val activity: MainActivity,
    private val binding : ActivityMainBinding,
) {

    val banner   = BannerAdManager(activity, binding)
    val rewarded = RewardedAdManager(activity)

    private var isAdsRemoved = false

    fun initialize() {
        MobileAds.initialize(activity) { initializationStatus ->
            val statusMap = initializationStatus.adapterStatusMap
            statusMap.forEach { (adapter, status) ->
                log("Adapter: $adapter | State: ${status.initializationState} | Description: ${status.description}")
            }
        }
        rewarded.load()
    }

    fun removeAds() {
        isAdsRemoved = true
        banner.destroy()
    }

    fun onResume()  { if (!isAdsRemoved) banner.onResume() }
    fun onPause()   { if (!isAdsRemoved) banner.onPause() }
    fun onDestroy() { banner.destroy(); rewarded.destroy() }
}