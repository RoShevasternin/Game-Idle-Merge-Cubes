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

    private val aPanelTopMenu = APanelTopMenu(screen)
    private val aBgImg        = Image(screen.drawerUtil.getTexture(GameColor.purple_350080))
    //private val aContent = APanelMenuContent(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelTopMenu()
        addBgImg()


        val aSettingsSection = ASettingsSection(screen)
        aSettingsSection.setSize(1916f, 1529f)

        add(aSettingsSection) { center() }

//        addActorWithConstraints(aSettingsSection) {
//            startToStartOf   = this@APanelMenu
//            endToEndOf       = this@APanelMenu
//            topToTopOf       = this@APanelMenu
//            bottomToBottomOf = this@APanelMenu
//        }

        aSettingsSection.addAction(Actions.sequence(
            Actions.delay(5f),
            Actions.forever(Actions.sequence(
                Actions.sizeBy(0f, 500f, 2f),
                Actions.sizeBy(0f, -500f, 2f),
            ))
        ))

        //aSettingsSection.animHeight(5000f)

        //aContent.onContentResized = { growMenu() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanelTopMenu() {
        aPanelTopMenu.setSize(width, 289f)
        aPanelTopMenu.y = height - aPanelTopMenu.height
        addActor(aPanelTopMenu)
    }

    private fun addBgImg() {
        val newHeight = (height - aPanelTopMenu.height)
        aBgImg.setSize(width, newHeight)
        addActor(aBgImg)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------
    fun animShowMenu() {
        log("d = $y")
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

    // ── Ріст меню при розкритті секцій ───────────────────────────────────────

//    private fun growMenu() {
//        val desired = aContent.totalContentH() + topBarH
//        val target  = desired.coerceIn(minMenuH, maxMenuH)
//        if (target == curMenuH) return
//        curMenuH = target
//        animMenuHeight(target)
//    }
//
//    private fun animMenuHeight(targetH: Float) {
//        val startH = aBg.height
//        clearActions()
//        addAction(object : Action() {
//            private var t = 0f
//            override fun act(delta: Float): Boolean {
//                t = (t + delta / 0.25f).coerceAtMost(1f)
//                val h = Interpolation.sineOut.apply(startH, targetH, t)
//
//                aBg.height      = h
//                aContent.height = h - topBarH
//                aContent.relayout()
//                aPanelTopMenu.setPosition(0f, h - topBarH)
//
//                return t >= 1f
//            }
//        })
//    }
}