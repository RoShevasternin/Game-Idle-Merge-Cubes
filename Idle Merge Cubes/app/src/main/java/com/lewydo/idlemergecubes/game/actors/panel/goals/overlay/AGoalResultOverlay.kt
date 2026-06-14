package com.lewydo.idlemergecubes.game.actors.panel.goals.overlay

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.enable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  AGoalResultOverlay — повноекранний оверлей результату задачі
//
//   COMPLETED → зелений фон, ✓ іконка, "+reward"
//   FAILED    → червоний фон, ✗ іконка, "Failed!"
// ═════════════════════════════════════════════════════════════════════════════

class AGoalResultOverlay(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Color
    // ------------------------------------------------------------------------
    private val COLOR_COMPLETED = Color.valueOf("158040")
    private val COLOR_FAILED    = Color.valueOf("8B1020")

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val fResult = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(78).setBorder(3f, Color.BLACK)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBg     = Image(screen.drawerUtil.getTexture(GameColor.green_98FF68))
    private val aIconOk = Image(gdxGame.assetsAll.coin)
    private val aIconX  = Image(gdxGame.assetsAll.bag_coins)
    private val aLbl    = Label("", FontFactory.create(screen, fResult, screen.fontGenerator_Nunito_Black, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBg()
        addIcons()
        addLbl()

        hideImmediate()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBg() {
        add(aBg) { fillParent() }
        aBg.color.a = 0f
    }

    private fun addIcons() {
        aIconOk.setSize(160f, 160f)
        add(aIconOk) { centerX(); centerY() }
        aIconOk.isVisible = false

        aIconX.setSize(160f, 160f)
        add(aIconX) { centerX(); centerY() }
        aIconX.isVisible = false
    }

    private fun addLbl() {
        aLbl.setSize(width, 90f)
        add(aLbl) { centerX(); bottomToBottom(margin = 60f) }
        aLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    fun showCompleted(reward: Long) {
        aBg.color.set(COLOR_COMPLETED); aBg.color.a = 0f
        aIconOk.isVisible = true
        aIconX.isVisible  = false
        aLbl.setText("+${NumberFormatter.format(reward)}")
        animShow()
    }

    fun showFailed() {
        aBg.color.set(COLOR_FAILED); aBg.color.a = 0f
        aIconOk.isVisible = false
        aIconX.isVisible  = true
        aLbl.setText("Failed!")
        animShow()
    }

    fun hideImmediate() {
        clearActions()
        isVisible = false
        disable()
        aBg.color.a = 0f
        aIconOk.isVisible = false
        aIconX.isVisible  = false
        aIconOk.setScale(1f)
        aIconX.setScale(1f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animShow() {
        isVisible = true
        enable()

        // Фон fade-in
        aBg.clearActions()
        aBg.addAction(Actions.fadeIn(0.3f))

        // Іконка — bounce
        val icon = if (aIconOk.isVisible) aIconOk else aIconX
        icon.setScale(0f)
        icon.clearActions()
        icon.addAction(Actions.sequence(
            Actions.delay(0.1f),
            Actions.scaleTo(1.25f, 1.25f, 0.22f, Interpolation.swingOut),
            Actions.scaleTo(1.0f,  1.0f,  0.14f, Interpolation.sineOut),
        ))
    }

}