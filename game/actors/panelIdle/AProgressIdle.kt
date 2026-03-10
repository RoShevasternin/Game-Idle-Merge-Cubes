package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.progress.ACircleProgress
import com.lewydo.idlemergecubes.game.actors.shader.AMask
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AProgressIdle(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackgroundImg = Image(gdxGame.assetsAll.idle_progress_background)
    private val aLavaProgress  = ALavaProgress(screen)
    private val aMask          = AMask(screen, gdxGame.assetsAll.MASK_PROGRESS_IDLE)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinished: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBackgroundImg()
        addMask()

        aMask.addAndFillActor(ACircleProgress(screen, 50f))
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBackgroundImg() {
        addAndFillActor(aBackgroundImg)
    }

    private fun addMask() {
        addActor(aMask)
        aMask.setBounds(12f, 10f, 1548f, 59f)

        aMask.addLavaProgress()
    }

    private fun Group.addLavaProgress() {
        addAndFillActor(aLavaProgress)
        toStart()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun toStart() {
        aLavaProgress.x = -aLavaProgress.width
        aLavaProgress.reset()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    fun startFill(seconds: Float) {
        toStart()

        aLavaProgress.addAction(Actions.sequence(
            Actions.moveTo(0f, 0f, seconds, Interpolation.linear),
            Actions.run {
                aLavaProgress.finish()
                onFinished.invoke()
            }
        ))
    }

}