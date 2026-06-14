package com.lewydo.idlemergecubes.game.screens

import com.lewydo.idlemergecubes.game.actors.brand.AMainBrand
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.manager.SpriteManager
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class BrandScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aMain by lazy { AMainBrand(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        stageUI.root.color.a = 0f

        loadBandAssets()
        setBackBackground(gdxGame.assetsBrand.BACKGROUND)
        super.show()

        animShowScreen {
            aMain.playIntroAnimation {
                //stageUI.root.animDelay(1f) {
                    animHideScreen { gdxGame.navigationManager.navigate(LoaderScreen::class.java.name) }
                //}
            }
        }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        aMain.setSize(WIDTH, HEIGHT)
        add(aMain) { center() }
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun loadBandAssets() {
        with(gdxGame.spriteManager) {
            loadableAtlasList = mutableListOf(SpriteManager.EnumAtlas.BRAND.data)
            loadAtlas()
            loadableTexturesList = mutableListOf(SpriteManager.EnumTexture.BRAND_BACKGROUND.data)
            loadTexture()
        }
//        with(gdxGame.soundManager) {
//            loadableSoundList = SoundManager.EnumSound.entries.map { it.data }.toMutableList()
//            load()
//        }
        gdxGame.assetManager.finishLoading()
        gdxGame.spriteManager.initAll()
//        gdxGame.soundManager.init()
    }


}