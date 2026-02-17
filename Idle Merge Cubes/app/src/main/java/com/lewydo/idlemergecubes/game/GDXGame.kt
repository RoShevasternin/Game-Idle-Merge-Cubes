package com.lewydo.idlemergecubes.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.lewydo.idlemergecubes.MainActivity
import com.lewydo.idlemergecubes.game.dataStore.DS_Player
import com.lewydo.idlemergecubes.game.manager.MusicManager
import com.lewydo.idlemergecubes.game.manager.NavigationManager
import com.lewydo.idlemergecubes.game.manager.ParticleEffectManager
import com.lewydo.idlemergecubes.game.manager.SoundManager
import com.lewydo.idlemergecubes.game.manager.SpriteManager
import com.lewydo.idlemergecubes.game.manager.util.MusicUtil
import com.lewydo.idlemergecubes.game.manager.util.ParticleEffectUtil
import com.lewydo.idlemergecubes.game.manager.util.SoundUtil
import com.lewydo.idlemergecubes.game.manager.util.SpriteUtil
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.screens.LoaderScreen
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGame
import com.lewydo.idlemergecubes.game.utils.disposeAll
import com.lewydo.idlemergecubes.util.currentClassName
import com.lewydo.idlemergecubes.util.log
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

    val ds_Player = DS_Player(coroutine)

    val modelPlayer = PlayerModel(ds_Player, coroutine)

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