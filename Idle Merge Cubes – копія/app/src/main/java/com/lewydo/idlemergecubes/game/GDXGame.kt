package com.lewydo.idlemergecubes.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
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
import com.lewydo.idlemergecubes.game.manager.util.VibroUtil
import com.lewydo.idlemergecubes.game.model.GridModel
import com.lewydo.idlemergecubes.game.model.IdleModel
import com.lewydo.idlemergecubes.game.model.LevelUpRewardModel
import com.lewydo.idlemergecubes.game.model.OfflineRewardModel
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.screens.LoaderScreen
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.Settings
import com.lewydo.idlemergecubes.game.utils.ShaderClock
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

    val musicUtil by lazy { MusicUtil() }
    val soundUtil by lazy { SoundUtil() }
    val vibroUtil by lazy { VibroUtil() }

    val particleEffectLoader by lazy { ParticleEffectUtil.Loader() }
    val particleEffectAll    by lazy { ParticleEffectUtil.All() }

    val settings by lazy { Settings() }

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    val coroutine = CoroutineScope(Dispatchers.Default)

    val ds_Player = DS_Player(coroutine)

    val modelPlayer        = PlayerModel(ds_Player, coroutine)
    val modelGrid          = GridModel(ds_Player, coroutine)
    val modelIdle          = IdleModel(modelGrid, modelPlayer, coroutine)
    val modelOfflineReward = OfflineRewardModel(modelPlayer)
    val modelLevelUp       = LevelUpRewardModel(modelPlayer)

    override fun create() {
        navigationManager = NavigationManager(this)
        assetManager      = AssetManager()
        spriteManager     = SpriteManager(assetManager)

        musicManager      = MusicManager(assetManager)
        soundManager      = SoundManager(assetManager)

        particleEffectManager = ParticleEffectManager(assetManager)

        navigationManager.navigate(LoaderScreen::class.java.name)

        ShaderProgram.pedantic = false
    }

    override fun render() {
        ShaderClock.update()

        ScreenUtils.clear(backgroundColor)
        super.render()
    }

    override fun pause() {
        super.pause()
        log("pause")
        modelOfflineReward.saveLoginTime()
    }

    override fun dispose() {
        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(assetManager, musicUtil)

            modelOfflineReward.saveLoginTime()

            log("dispose $currentClassName")
            super.dispose()
        } catch (e: Exception) { log("exception: ${e.message}") }
    }

}