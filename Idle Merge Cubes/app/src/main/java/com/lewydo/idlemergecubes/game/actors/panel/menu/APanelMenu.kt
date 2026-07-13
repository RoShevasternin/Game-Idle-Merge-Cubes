package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.animMoveTo
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.enable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelMenu(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    private val maxMenuH get() = screen.stageUI.height - screen.safeStatusBarUI

    // ── Actors ────────────────────────────────────────────────────────────────

    private val aPanelTopMenu     = APanelTopMenu(screen)
    private val aBgImg            = Image(screen.drawerUtil.getTexture(GameColor.purple_350080))
    private val aPanelContentMenu = APanelContentMenu(screen)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------

    private val contentTopMargin    = 78f
    private val contentBottomMargin = 55f

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var blockLeaderboard  = {}
    var blockClose        = {}
    var blockClearGrid    = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelTopMenu()
        addBgImg()
        addPanelContentMenu()

        aPanelTopMenu.toFront()

        setupHeightCallback()

//        addAction(Actions.sequence(
//            Actions.delay(3f),
//            Actions.forever(Actions.sequence(
//                Actions.sizeBy(0f, 500f, 3f),
//                Actions.sizeBy(0f, -500f, 3f),
//            ))
//        ))
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanelTopMenu() {
        aPanelTopMenu.setSize(width, 289f)
        add(aPanelTopMenu) { topToTop() }
    }

    private fun addBgImg() {
        aBgImg.width = width
        add(aBgImg) {
            matchConstraint()
            topToBottom(aPanelTopMenu, -20f)
            startToStart()
            endToEnd()
            bottomToBottom()
        }
    }

    private fun addPanelContentMenu() {
        aPanelContentMenu.width = 1916f
        add(aPanelContentMenu) {
            matchHeight()
            topToBottom(aPanelTopMenu, contentTopMargin)
            startToStart(margin = 122f)
            endToEnd(margin = 122f)
            bottomToBottom(margin = contentBottomMargin)
        }

        aPanelContentMenu.blockLeaderboard = { blockLeaderboard() }
        aPanelContentMenu.blockClose       = { blockClose() }
        aPanelContentMenu.blockClearGrid   = { blockClearGrid() }
    }

    // ── Height callback ───────────────────────────────────────────────────────
    //
    // Ланцюжок:
    //   aSettingsSection росте
    //     → aScrollContent (wrap=true) росте
    //     → APanelContentMenu.act() → onHeightChanged(totalH)
    //     → тут рахуємо нову висоту APanelMenu з обмеженням maxMenuH
    //     → height = newH  →  AConstraintLayout перерахує matchHeight/matchConstraint
    //     → aBgImg і aPanelContentMenu автоматично підлаштовуються
    //     → aScrollPane всередині aPanelContentMenu теж (matchHeight)
    //     → якщо контент > aScrollPane → ScrollPane вмикає скрол ✓

    private fun setupHeightCallback() {
        aPanelContentMenu.onHeightChanged = { totalContentH ->
            val desiredH = aPanelTopMenu.height + contentTopMargin + totalContentH + contentBottomMargin
            val newH     = desiredH.coerceAtMost(maxMenuH)
            if (newH != height) height = newH
        }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    fun animShowMenu(time: Float) {
        enable()
        clearActions()
        animMoveTo(
            x             = x,
            y             = 0f,
            time          = time,
            interpolation = Interpolation.sineOut
        )
    }

    fun animHideMenu(time: Float, onDone: () -> Unit = {}) {
        clearActions()
        disable()
        animMoveTo(
            x             = x,
            y             = -height,
            time          = time,
            interpolation = Interpolation.sineIn
        ) {
            onDone()
        }
    }

}