package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panelMenu.settings.ASettingsSection
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.animMoveTo
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.enable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.util.log

class APanelMenu(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    private val maxMenuH get() = screen.stageUI.height - screen.safeTopUI

    // ── Actors ────────────────────────────────────────────────────────────────

    private val aPanelTopMenu     = APanelTopMenu(screen)
    private val aBgImg            = Image(screen.drawerUtil.getTexture(GameColor.purple_350080))
    private val aPanelContentMenu = APanelContentMenu(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        debug()

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
            topToBottom(aPanelTopMenu)
            startToStart()
            endToEnd()
            bottomToBottom()
        }
    }

    private fun addPanelContentMenu() {
        aPanelContentMenu.debug()

        aPanelContentMenu.width = 1916f
        add(aPanelContentMenu) {
            matchHeight()
            topToBottom(aPanelTopMenu, 78f)
            startToStart(margin = 122f)
            endToEnd(margin = 122f)
            bottomToBottom(margin = 55f)
        }
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
            val desiredH = aPanelTopMenu.height + 78f + totalContentH + 55f
            val newH     = desiredH.coerceAtMost(maxMenuH)
            if (newH != height) height = newH
        }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    fun animShowMenu() {
        enable()
        clearActions()
        animMoveTo(
            x             = x,
            y             = 0f,
            time          = 0.35f,
            interpolation = Interpolation.sineOut
        )
    }

    fun animHideMenu(onDone: () -> Unit = {}) {
        clearActions()
        disable()
        animMoveTo(
            x             = x,
            y             = -height,
            time          = 0.28f,
            interpolation = Interpolation.sineIn
        ) {
            onDone()
        }
    }

}