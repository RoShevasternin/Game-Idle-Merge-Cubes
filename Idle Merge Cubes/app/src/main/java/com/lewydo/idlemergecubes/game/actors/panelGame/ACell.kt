package com.lewydo.idlemergecubes.game.actors.panelGame

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.utils.Acts
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACell(
    override val screen: AdvancedScreen,
    val index: Int
): AdvancedGroup() {

    private val aCellDefaultImg = Image(gdxGame.assetsAll.cell_def)
    private val aCellRedImg     = Image(gdxGame.assetsAll.cell_red)
    private val aCellGreenImg   = Image(gdxGame.assetsAll.cell_green)

    private var currentState = State.Def

    override fun addActorsOnGroup() {
        addCellImg()
    }

    // Actors ------------------------------------------------------------------------

    private fun addCellImg() {
        addAndFillActor(aCellDefaultImg)
        addActors(aCellRedImg)
        addActors(aCellGreenImg)

        aCellRedImg.setBounds(-50f, -50f, 392f, 392f)
        aCellGreenImg.setBounds(-50f, -50f, 392f, 392f)

        aCellRedImg.color.a   = 0f
        aCellGreenImg.color.a = 0f
    }

    // Logic ------------------------------------------------------------------------

    fun setState(state: State) {

        if (state == currentState) return
        currentState = state

        hideAll()
        setScale(1f)

        when (state) {
            State.Def   -> animFadeIn(aCellDefaultImg)
            State.Red   -> animFadeIn(aCellRedImg)
            State.Green -> animFadeIn(aCellGreenImg)

            State.StartPos -> {
                // змінюй колір ячейки на колір куба, це ячейка з якої прибрали куб
                animFadeIn(aCellDefaultImg)
            }
            State.Target -> {
                // змінюй розмір ячейки бо вона пуста і над нею куб
                animFadeIn(aCellDefaultImg)
                aCellDefaultImg.setScale(0.8f)
            }
        }
    }

    fun reset() {
        setState(State.Def)
    }

    private fun hideAll() {
        animFadeOut(aCellDefaultImg)
        animFadeOut(aCellRedImg)
        animFadeOut(aCellGreenImg)
    }

    // Anim ------------------------------------------------------------------------

    private fun animFadeIn(image: Image) {
        if (image.color.a == 1f) return
        image.clearActions()
        image.addAction(Acts.fadeIn(0.1f, Interpolation.fade))
    }

    private fun animFadeOut(image: Image) {
        if (image.color.a == 0f) return
        image.clearActions()
        image.addAction(Acts.fadeOut(0.1f, Interpolation.fade))
    }


    enum class State {
        Def, Red, Green, StartPos, Target
    }

}