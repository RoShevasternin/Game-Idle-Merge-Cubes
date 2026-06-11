package com.lewydo.idlemergecubes.game.actors.panel.mergeBonus

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.vfx.AMask
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AProgressMergeBonus(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackgroundImg = Image(gdxGame.assetsAll.progress_background_merge_bonus)
    private val aLavaProgress  = ALavaProgress(screen)
    private val aMask          = AMask(screen, gdxGame.assetsAll.MASK_PROGRESS_MERGE_BONUS)

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
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBackgroundImg() {
        addAndFillActor(aBackgroundImg)
    }

    private fun addMask() {
        addActor(aMask)
        aMask.setBounds(12f, 9f, 1546f, 57f)

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

    fun setProgress(pct: Float) {
        aLavaProgress.clearActions()  // ← чистимо саме aLavaProgress де живе onFinished

        val targetX = (pct * aLavaProgress.parent.width) - aLavaProgress.width
        aLavaProgress.addAction(
            Actions.moveTo(targetX.coerceAtLeast(-aLavaProgress.width), 0f, 0.5f, Interpolation.sineOut)
        )

        if (pct >= 1f) {
            aLavaProgress.addAction(
                Actions.sequence(
                    Actions.moveTo(0f, 0f, 0.3f, Interpolation.sineOut),
                    Actions.run { onFinished.invoke() }
                )
            )
        }
    }

}