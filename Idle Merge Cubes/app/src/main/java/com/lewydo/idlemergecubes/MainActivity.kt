package com.lewydo.idlemergecubes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.lewydo.idlemergecubes.services.ads.AdManager
import com.lewydo.idlemergecubes.databinding.ActivityMainBinding
import com.lewydo.idlemergecubes.util.OneTime
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        var statusBarHeight = 0
        var navBarHeight    = 0
    }

    private val coroutine  = CoroutineScope(Dispatchers.Default)
    private val onceExit   = OneTime()

    private val onceSystemBarHeight = OneTime()

    private lateinit var binding : ActivityMainBinding

    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    lateinit var adManager: AdManager
        private set

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            onceSystemBarHeight.use {
                statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

                log("statusBarHeight = $statusBarHeight | navBarHeight = $navBarHeight")

                // hide Status or Nav bar (після встановлення їх розмірів)
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    override fun exit() {
        onceExit.use {
            log("exit")
            coroutine.launch(Dispatchers.Main) {
                finishAndRemoveTask()
                delay(100)
                exitProcess(0)
            }
        }
    }

    override fun onResume()  { super.onResume();  adManager.onResume() }
    override fun onPause()   { super.onPause();   adManager.onPause() }
    override fun onDestroy() { super.onDestroy(); adManager.onDestroy() }

    private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeAds()
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------

    fun openEmail(to: String, subject: String = "") {
        runOnUiThread {
            val uri = "mailto:$to?subject=${Uri.encode(subject)}".toUri()

            val intent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(Intent.createChooser(intent, "Send email"))
        }
    }

    fun openInstagram(username: String) {
        runOnUiThread {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "instagram://user?username=$username".toUri()).setPackage("com.instagram.android"))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, "https://www.instagram.com/$username".toUri()))
            }
        }
    }

    fun openTelegram(username: String) {
        runOnUiThread {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "tg://resolve?domain=$username".toUri()).setPackage("org.telegram.messenger"))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, "https://t.me/$username".toUri()))
            }
        }
    }

    fun openPlayMarket() {
        runOnUiThread {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "market://dev?id=5953840215091948966".toUri()).setPackage("com.android.vending"))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/dev?id=5953840215091948966".toUri()))
            }
        }
    }

    // ------------------------------------------------------------------------
    // Ads
    // ------------------------------------------------------------------------

    private fun initializeAds() {
        adManager = AdManager(this, binding)
        adManager.initialize()
    }

}