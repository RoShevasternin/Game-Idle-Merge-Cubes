package com.lewydo.idlemergecubes.ads

import android.view.View
import com.google.android.gms.ads.*
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.MainActivity
import com.lewydo.idlemergecubes.R
import com.lewydo.idlemergecubes.databinding.ActivityMainBinding

class BannerAdManager(
    private val activity: MainActivity,
    private val binding : ActivityMainBinding,
) {

    companion object {
        private const val BANNER_ID = BuildConfig.ADMOB_BANNER_ID
    }

    private var adView: AdView? = null

    fun show() = activity.runOnUiThread {
        if (adView != null) {
            binding.bannerContainer.visibility = View.VISIBLE
            return@runOnUiThread
        }

        adView = AdView(activity).apply {
            adUnitId = BANNER_ID
            setAdSize(AdSize.BANNER)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    binding.bannerContainer.visibility = View.VISIBLE
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    binding.bannerContainer.visibility = View.GONE
                }
            }
            loadAd(AdRequest.Builder().build())
        }

        binding.bannerContainer.addView(adView)
    }

    fun hide() = activity.runOnUiThread { binding.bannerContainer.visibility = View.GONE }

    fun destroy() = activity.runOnUiThread {
        adView?.destroy()
        adView = null
        binding.bannerContainer.removeAllViews()
        binding.bannerContainer.visibility = View.GONE
    }

    fun onResume()  { adView?.resume() }
    fun onPause()   { adView?.pause() }
}