package com.lewydo.idlemergecubes.game.actors.panel.goals.overlay

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.animShowAndEnable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  AGoalResultOverlay — повноекранний оверлей результату задачі
//
//   COMPLETED → зелений фон, ✓ іконка, "+reward"
//   FAILED    → червоний фон, ✗ іконка, "Failed!"
// ═════════════════════════════════════════════════════════════════════════════

class AGoalResultOverlay(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Regions
    // ------------------------------------------------------------------------
    private val regionBgCompleted = TextureRegionDrawable(gdxGame.assetsAll.GOALS_RESULT_DONE)
    private val regionBgFailed    = TextureRegionDrawable(gdxGame.assetsAll.GOALS_RESULT_FAIL)

    private val regionIconCompleted = TextureRegionDrawable(gdxGame.assetsAll.goals_icon_done)
    private val regionIconFailed    = TextureRegionDrawable(gdxGame.assetsAll.goals_icon_fail)

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleResult = MsdfStyle(msdf, msdf.fontNunitoBold, 72f)
        .stroke(2f, Color.BLACK)
        .dropShadow(2f, 0f, 4f, Color.BLACK)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image()
    private val aIconImg   = Image()
    private val aResultLbl = AMsdfLabel("", styleResult)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addIconImg()
        addResultLbl()

        hideImmediate()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addIconImg() {
        aIconImg.setSize(213f, 213f)
        add(aIconImg) { centerX(); topToTop(margin = 41f) }
        aIconImg.setOrigin(Align.center)
    }

    private fun addResultLbl() {
        aResultLbl.setSize(width, 98f)
        add(aResultLbl) { centerX(); topToBottom(aIconImg, 24f) }
        aResultLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    fun showCompleted(reward: Long) {
        aBgImg.drawable   = regionBgCompleted
        aIconImg.drawable = regionIconCompleted
        aResultLbl.setText("+${NumberFormatter.format(reward)}")
        animShow()
    }

    fun showFailed() {
        aBgImg.drawable   = regionBgFailed
        aIconImg.drawable = regionIconFailed
        aResultLbl.setText("Failed!")
        animShow()
    }

    fun hideImmediate() {
        clearActions()
        color.a = 0f
        aIconImg.setScale(1f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animShow() {
        // Фон fade-in
        clearActions()
        animShowAndEnable(0.3f)

        // Іконка — bounce
        aIconImg.setScale(0f)
        aIconImg.clearActions()
        aIconImg.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.scaleTo(1.25f, 1.25f, 0.22f, Interpolation.swingOut),
            Actions.scaleTo(1.0f,  1.0f,  0.14f, Interpolation.sineOut),
        ))
    }

}