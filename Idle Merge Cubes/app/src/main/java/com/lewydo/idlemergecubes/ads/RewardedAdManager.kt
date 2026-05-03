package com.lewydo.idlemergecubes.ads

import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.lewydo.idlemergecubes.MainActivity
import com.lewydo.idlemergecubes.R

class RewardedAdManager(private val activity: MainActivity) {

    private var rewardedAd: RewardedAd? = null

    val isReady: Boolean get() = rewardedAd != null

    fun load() {
//        RewardedAd.load(activity, activity.getString(R.string.admob_banner_id), AdRequest.Builder().build(),
//            object : RewardedAdLoadCallback() {
//                override fun onAdLoaded(ad: RewardedAd) {
//                    rewardedAd = ad
//                }
//                override fun onAdFailedToLoad(error: LoadAdError) {
//                    rewardedAd = null
//                }
//            }
//        )
    }

    fun show(onEarned: () -> Unit, onDismissed: () -> Unit = {}) {
//        val ad = rewardedAd ?: run { onDismissed(); return }
//
//        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//                rewardedAd = null
//                load()
//                onDismissed()
//            }
//        }
//
//        ad.show(activity) { onEarned() }
    }

    fun destroy() { rewardedAd = null }
}