package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.actors.AMainLoader
import com.lewydo.idlemergecubes.game.manager.MusicManager
import com.lewydo.idlemergecubes.game.manager.ParticleEffectManager
import com.lewydo.idlemergecubes.game.manager.SoundManager
import com.lewydo.idlemergecubes.game.manager.SpriteManager
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.HEIGHT_UI
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.WIDTH_UI
import com.lewydo.idlemergecubes.game.utils.actor.HAlign
import com.lewydo.idlemergecubes.game.utils.actor.VAlign
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaderScreen : AdvancedScreen() {

    private val progressFlow     = MutableStateFlow(0f)
    private var isFinishLoading  = false
    private var isFinishProgress = false

    private val textBranding = """
        Powered by LibGDX
        Developed by Lewydo™
        Version ${BuildConfig.VERSION_NAME}
    """.trimIndent()

    private val parameter = FontParameter().setCharacters(textBranding).setSize(40)
    private val font      = fontGenerator_Nunito_SemiBold.generateFont(parameter)

    private val aMain by lazy { AMainLoader(this) }

    private val brandingLbl  = Label(textBranding, Label.LabelStyle(font, Color.WHITE.cpy().apply { a = 0.55f }))


    override fun show() {
        loadSplashAssets()
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()

        loadAssets()
        collectProgress()
    }

    override fun render(delta: Float) {
        super.render(delta)
        loadingAssets()
        isFinish()
    }

    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // Actors ------------------------------------------------------------------------

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        //aMain.debug()
        aMain.setSize(WIDTH_UI, HEIGHT_UI)
        addActorAligned(aMain, HAlign.CENTER, VAlign.CENTER)

        addBrandingLbl()

        animShowScreen()
    }

    private fun Group.addBrandingLbl() {
        brandingLbl.setSize(443f, 165f)
        addActorAligned(brandingLbl, HAlign.CENTER)
        brandingLbl.setAlignment(Align.center)
        brandingLbl.y = 120f
    }

    // Logic ------------------------------------------------------------------------

    private fun loadSplashAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = mutableListOf(SpriteManager.EnumAtlas.LOADER.data)
            loadAtlas()
            loadableTexturesList = mutableListOf(
                SpriteManager.EnumTexture.BACKGROUND.data,
                SpriteManager.EnumTexture.MASK.data,
                SpriteManager.EnumTexture.C1.data,
                SpriteManager.EnumTexture.C2.data,
                SpriteManager.EnumTexture.C3.data,
                SpriteManager.EnumTexture.C4.data,
                SpriteManager.EnumTexture.C5.data,
                SpriteManager.EnumTexture.C6.data,
            )
            loadTexture()
        }
        with(gdxGame.particleEffectManager) {
            loadableParticleEffectList = mutableListOf(ParticleEffectManager.EnumParticleEffect.Loader.data)
            load()
        }
        gdxGame.assetManager.finishLoading()
        gdxGame.spriteManager.initAtlasAndTexture()
        gdxGame.particleEffectManager.init()
    }

    private fun loadAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = SpriteManager.EnumAtlas.entries.map { it.data }.toMutableList()
            loadAtlas()
            loadableTexturesList = SpriteManager.EnumTexture.entries.map { it.data }.toMutableList()
            loadTexture()
        }
        with(gdxGame.musicManager) {
            loadableMusicList = MusicManager.EnumMusic.entries.map { it.data }.toMutableList()
            load()
        }
        with(gdxGame.soundManager) {
            loadableSoundList = SoundManager.EnumSound.entries.map { it.data }.toMutableList()
            load()
        }
        with(gdxGame.particleEffectManager) {
            loadableParticleEffectList = ParticleEffectManager.EnumParticleEffect.entries.map { it.data }.toMutableList()
            load()
        }
    }

    private fun initAssets() {
        gdxGame.spriteManager.initAtlasAndTexture()
        gdxGame.musicManager.init()
        gdxGame.soundManager.init()
        gdxGame.particleEffectManager.init()
    }

    private fun loadingAssets() {
        if (isFinishLoading.not()) {
            if (gdxGame.assetManager.update(16)) {
                isFinishLoading = true
                initAssets()
            }
            progressFlow.value = gdxGame.assetManager.progress
        }
    }

    private fun collectProgress() {
        coroutine?.launch {
            var progress = 0
            progressFlow.collect { p ->
                while (progress < (p * 100)) {
                    progress += 1

                    runGDX {
                        aMain.aLoading.setProgressPercent(progress.toFloat())
                    }

                    if (progress % 50 == 0) log("progress = $progress%")
                    if (progress == 100) isFinishProgress = true

                    //delay((25..35).shuffled().first().toLong())
                }
            }
        }
    }

    private fun isFinish() {
        if (isFinishProgress) {
            isFinishProgress = false


//            game.musicUtil.apply { music = main.apply {
//                isLooping = true
//                coff      = 0.15f
//            } }


            stageUI.root.animDelay(1f) {
                aMain.aLightLoader.onLoaderFinish()
                animHideScreen { gdxGame.navigationManager.navigate(GameScreen::class.java.name) }
            }
        }
    }


}