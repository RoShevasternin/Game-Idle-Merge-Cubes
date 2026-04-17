package com.lewydo.idlemergecubes.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.setOrigin
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACircleStrokeFill(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aCircleStrokeImg = Image(gdxGame.assetsAll.circle_stroke)
    private val aCircleFillImg   = Image(gdxGame.assetsAll.circle_fill)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addCircleStrokeImg()
        addCircleFillImg()

        animCircleFill()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCircleStrokeImg() {
        addAndFillActor(aCircleStrokeImg)
    }

    private fun addCircleFillImg() {
        val size = width * 0.8641f
        aCircleFillImg.setSize(size, size)
        addActorAligned(aCircleFillImg, AlignH.CENTER, AlignV.CENTER)

        aCircleFillImg.setOrigin(Align.center)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animCircleFill() {
        aCircleFillImg.addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(0.95f, 0.95f, 0.4f, Interpolation.sine),
            Actions.scaleTo(1.0f, 1.0f, 0.45f, Interpolation.sine),
        )))
    }

}