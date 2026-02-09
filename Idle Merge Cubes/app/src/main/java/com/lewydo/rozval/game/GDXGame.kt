package com.lewydo.rozval.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.lewydo.rozval.MainActivity
import com.lewydo.rozval.game.manager.*
import com.lewydo.rozval.game.manager.util.MusicUtil
import com.lewydo.rozval.game.manager.util.ParticleEffectUtil
import com.lewydo.rozval.game.manager.util.SoundUtil
import com.lewydo.rozval.game.manager.util.SpriteUtil
import com.lewydo.rozval.game.screens.LoaderScreen
import com.lewydo.rozval.game.utils.GameColor
import com.lewydo.rozval.game.utils.advanced.AdvancedGame
import com.lewydo.rozval.game.dataStore.DS_IsTutorial
import com.lewydo.rozval.game.dataStore.DS_Key
import com.lewydo.rozval.game.dataStore.DS_Location
import com.lewydo.rozval.game.dataStore.DS_Star
import com.lewydo.rozval.game.utils.disposeAll
import com.lewydo.rozval.util.currentClassName
import com.lewydo.rozval.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class GDXGame(val activity: MainActivity) : AdvancedGame() {

    lateinit var assetManager     : AssetManager      private set
    lateinit var navigationManager: NavigationManager private set
    lateinit var spriteManager    : SpriteManager     private set
    lateinit var musicManager     : MusicManager      private set
    lateinit var soundManager     : SoundManager      private set
    lateinit var particleEffectManager: ParticleEffectManager private set

    val assetsLoader by lazy { SpriteUtil.Loader() }
    val assetsAll    by lazy { SpriteUtil.All() }

    val musicUtil by lazy { MusicUtil()    }
    val soundUtil by lazy { SoundUtil()    }

    val particleEffectUtil by lazy { ParticleEffectUtil() }

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    val coroutine = CoroutineScope(Dispatchers.Default)

    val ds_star       = DS_Star(coroutine)
    val ds_key        = DS_Key(coroutine)
    val ds_Location   = DS_Location(coroutine)
    val ds_isTutorial = DS_IsTutorial(coroutine)

    override fun create() {
        navigationManager = NavigationManager(this)
        assetManager      = AssetManager()
        spriteManager     = SpriteManager(assetManager)

        musicManager      = MusicManager(assetManager)
        soundManager      = SoundManager(assetManager)

        particleEffectManager = ParticleEffectManager(assetManager)

        navigationManager.navigate(LoaderScreen::class.java.name)
    }

    override fun render() {
        ScreenUtils.clear(backgroundColor)
        super.render()
    }

    override fun dispose() {
        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(assetManager, musicUtil)

            log("dispose $currentClassName")
            super.dispose()
        } catch (e: Exception) { log("exception: ${e.message}") }
    }

}