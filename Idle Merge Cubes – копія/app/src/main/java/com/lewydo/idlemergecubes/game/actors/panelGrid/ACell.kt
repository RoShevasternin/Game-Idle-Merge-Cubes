package com.lewydo.idlemergecubes.game.actors.panelGrid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActors
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACell(
    override val screen: AdvancedScreen,
    val index: Int
): AdvancedGroup() {

    private val timeAnim = 0.12f

    // ------------------------------------------------------------------------
    // ACTORS
    // ------------------------------------------------------------------------

    private val aCellDefaultImg = Image(gdxGame.assetsAll.cell_def)
    private val aCellTintImg    = Image(gdxGame.assetsAll.cell_tint)
    private val aCellRedImg     = Image(gdxGame.assetsAll.cell_red)
    private val aCellGreenImg   = Image(gdxGame.assetsAll.cell_green)

    private val listStateImg = listOf(aCellDefaultImg, aCellTintImg, aCellGreenImg, aCellRedImg)

    private var currentState = State.DEFAULT


    // ------------------------------------------------------------------------
    // LIFECYCLE
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        color.a = 0f
        setOrigin(Align.center)

        listStateImg.forEach { it.color.a = 0f }
        aCellDefaultImg.color.a = 1f

        addCellImg()
        animShow(0.2f)
    }

    // ------------------------------------------------------------------------
    // ADD ACTORS
    // ------------------------------------------------------------------------

    private fun addCellImg() {
        addAndFillActors(aCellDefaultImg, aCellTintImg)
        addActors(aCellRedImg, aCellGreenImg)

        aCellRedImg.setBounds(-50f, -50f, 492f, 494f)
        aCellGreenImg.setBounds(-50f, -50f, 492f, 494f)

        aCellDefaultImg.setOrigin(Align.center)
        aCellTintImg.setOrigin(Align.center)
    }

    // ------------------------------------------------------------------------
    // STATE LOGIC
    // ------------------------------------------------------------------------

    fun setState(state: State, tintColor: Color? = null) {

        if (state == currentState) return
        currentState = state

        animHideAll()

        when (state) {

            State.DEFAULT -> {
                fadeIn(aCellDefaultImg)
                animateScale(aCellDefaultImg, 1f)
            }

            State.HOVER_EMPTY -> {
                fadeIn(aCellDefaultImg)
                animateScale(aCellDefaultImg, 0.9f)
            }

            State.START -> {
                aCellTintImg.color = Color(tintColor ?: Color.WHITE).apply { mul(0.6f, 0.6f, 0.6f, 1f) }
                fadeIn(aCellTintImg)
                animateScale(aCellTintImg, 0.9f)
            }

            State.HOVER_MATCH -> {
                fadeIn(aCellGreenImg)
            }

            State.HOVER_INVALID -> {
                fadeIn(aCellRedImg)
            }
        }
    }

    // ------------------------------------------------------------------------
    // ANIMATIONS
    // ------------------------------------------------------------------------

    private fun animHideAll() {
        listStateImg.forEach {
            fadeOut(it)
            animateScale(it, 1f)
        }
    }

    private fun animateScale(image: Image, targetScale: Float) {
        if (image.scaleX == targetScale) return
        image.addAction(
            Actions.scaleTo(
                targetScale,
                targetScale,
                0.12f,
                Interpolation.sineOut
            )
        )
    }

    private fun fadeIn(image: Image, isClearActions: Boolean = true) {
        if (isClearActions) image.clearActions()
        image.addAction(Actions.fadeIn(0.12f))
    }

    private fun fadeOut(image: Image, isClearActions: Boolean = true) {
        if (isClearActions) image.clearActions()
        image.addAction(Actions.fadeOut(0.12f))
    }

    // ------------------------------------------------------------------------
    // STATES
    // ------------------------------------------------------------------------

    enum class State {
        DEFAULT,
        START,
        HOVER_EMPTY,
        HOVER_MATCH,
        HOVER_INVALID
    }

}