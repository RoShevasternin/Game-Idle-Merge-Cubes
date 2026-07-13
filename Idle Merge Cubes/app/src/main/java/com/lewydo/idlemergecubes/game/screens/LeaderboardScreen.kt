package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class LeaderboardScreen: AdvancedScreen() {

    private val textTitle = "LEADERBOARD"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterTitle = FontParameter().setCharacters(textTitle).setSize(160)

    private val lsTitle by lazy { FontFactory.create(this, parameterTitle, fontGenerator_Nunito_SemiBold) }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aBackBtn      by lazy { AButtonTexture(this, AButtonStyles.Texture.BACK) }
    private val aTitleLbl     by lazy { Label(textTitle, lsTitle) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()
        animShowScreen()

        // відправляємо актуальний XP і одразу відкриваємо стандартний UI Google
        gdxGame.activity.submitXp(gdxGame.modelPlayer.currentXp)
        gdxGame.activity.showLeaderboard()

        stageUI.root.animDelay(0.07f) { gdxGame.navigationManager.back() }
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addBackBtn()
        addTitleLbl()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addBackBtn() {
        aBackBtn.setSize(236f, 236f)
        add(aBackBtn) { startToStart(margin = 128f); topToTop(margin = 78f) }
        aBackBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addTitleLbl() {
        aTitleLbl.setSize(1876f, 218f)
        add(aTitleLbl) { centerX(); topToBottom(aBackBtn) }
        aTitleLbl.setAlignment(Align.center)
    }

}