package com.lewydo.idlemergecubes.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.lewydo.idlemergecubes.MainActivity
import com.lewydo.idlemergecubes.services.analytics.FirebaseAnalyticsProvider
import com.lewydo.idlemergecubes.game.manager.MusicManager
import com.lewydo.idlemergecubes.game.manager.NavigationManager
import com.lewydo.idlemergecubes.game.manager.ParticleEffectManager
import com.lewydo.idlemergecubes.game.manager.SoundManager
import com.lewydo.idlemergecubes.game.manager.SpriteManager
import com.lewydo.idlemergecubes.game.manager.TutorialManager
import com.lewydo.idlemergecubes.game.manager.util.MusicUtil
import com.lewydo.idlemergecubes.game.manager.util.ParticleEffectUtil
import com.lewydo.idlemergecubes.game.manager.util.SoundUtil
import com.lewydo.idlemergecubes.game.manager.util.SpriteUtil
import com.lewydo.idlemergecubes.game.manager.util.VibroUtil
import com.lewydo.idlemergecubes.game.model.BuyLevelModel
import com.lewydo.idlemergecubes.game.model.GoalsModel
import com.lewydo.idlemergecubes.game.model.GridModel
import com.lewydo.idlemergecubes.game.model.LevelUpRewardModel
import com.lewydo.idlemergecubes.game.model.MergeBonusModel
import com.lewydo.idlemergecubes.game.model.OfflineRewardModel
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.screens.BrandScreen
import com.lewydo.idlemergecubes.game.screens.LoaderScreen
import com.lewydo.idlemergecubes.game.state.GameState
import com.lewydo.idlemergecubes.game.state.SaveGameStateManager
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.Settings
import com.lewydo.idlemergecubes.game.utils.ShaderClock
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGame
import com.lewydo.idlemergecubes.game.utils.disposeAll
import com.lewydo.idlemergecubes.game.utils.vfx.Blit
import com.lewydo.idlemergecubes.game.utils.vfx.VfxShaderCache
import com.lewydo.idlemergecubes.services.analytics.AnalyticsManager
import com.lewydo.idlemergecubes.util.currentClassName
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class GDXGame(val activity: MainActivity) : AdvancedGame() {

    // ------------------------------------------------------------------------
    // Assets
    // ------------------------------------------------------------------------

    val assetsBrand  by lazy { SpriteUtil.Brand() }
    val assetsLoader by lazy { SpriteUtil.Loader() }
    val assetsAll    by lazy { SpriteUtil.All() }

    val particleEffectLoader by lazy { ParticleEffectUtil.Loader() }
    val particleEffectAll    by lazy { ParticleEffectUtil.All() }

    // ------------------------------------------------------------------------
    // Audio
    // ------------------------------------------------------------------------

    val musicUtil by lazy { MusicUtil() }
    val soundUtil by lazy { SoundUtil() }
    val vibroUtil by lazy { VibroUtil() }

    // ------------------------------------------------------------------------
    // Managers
    // ------------------------------------------------------------------------

    lateinit var assetManager         : AssetManager          private set
    lateinit var navigationManager    : NavigationManager     private set
    lateinit var spriteManager        : SpriteManager         private set
    lateinit var musicManager         : MusicManager          private set
    lateinit var soundManager         : SoundManager          private set
    lateinit var particleEffectManager: ParticleEffectManager private set

    // ------------------------------------------------------------------------
    // Coroutine
    // ------------------------------------------------------------------------

    val coroutine = CoroutineScope(Dispatchers.Default)

    // ------------------------------------------------------------------------
    // GameState
    // ------------------------------------------------------------------------

    private val gameState   = GameState()
    private val saveManager = SaveGameStateManager(gameState, coroutine)

    // ------------------------------------------------------------------------
    // Models
    // ------------------------------------------------------------------------

    val modelPlayer        = PlayerModel(gameState, coroutine)
    val modelGrid          = GridModel(gameState)
    val modelMergeBonus    = MergeBonusModel(gameState, modelGrid, modelPlayer, coroutine)
    val modelOfflineReward = OfflineRewardModel(modelPlayer)
    val modelLevelUp       = LevelUpRewardModel(modelPlayer)
    val modelBuyLevel      = BuyLevelModel(gameState, coroutine)
    val modelGoals         = GoalsModel(gameState, modelPlayer, modelBuyLevel, coroutine)

    // ------------------------------------------------------------------------
    // Services
    // ------------------------------------------------------------------------

    val settings        by lazy { Settings() }
    val analytics       by lazy { AnalyticsManager() }
    val tutorialManager by lazy { TutorialManager(modelPlayer) }

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun create() {
        assetManager          = AssetManager()
        spriteManager         = SpriteManager(assetManager)
        musicManager          = MusicManager(assetManager)
        soundManager          = SoundManager(assetManager)
        particleEffectManager = ParticleEffectManager(assetManager)
        navigationManager     = NavigationManager(this)

        saveManager.load()
        saveManager.startAutoSave(intervalSec = 30)

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
        saveManager.save()
        modelOfflineReward.saveLoginTime()
        modelGoals.pauseTimer()
    }

    override fun resume() {
        super.resume()
        log("resume")
        Blit.dispose()
    }

    override fun dispose() {
        saveManager.stopAutoSave()
        saveManager.save()

        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(assetManager, musicUtil, VfxShaderCache, Blit)
            modelOfflineReward.saveLoginTime()
            log("dispose $currentClassName")
            super.dispose()
        } catch (e: Exception) {
            log("exception: ${e.message}")
        }
    }

}