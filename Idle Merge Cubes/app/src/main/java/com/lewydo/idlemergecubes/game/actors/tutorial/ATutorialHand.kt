package com.lewydo.idlemergecubes.game.actors.tutorial

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ATutorialHand(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aHandImg = Image(gdxGame.assetsAll.coin)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        setOrigin(Align.center)
        addAndFillActor(aHandImg)
    }

    // ------------------------------------------------------------------------
    // Tap — Step BUY
    // ------------------------------------------------------------------------

    fun showTap(stageX: Float, stageY: Float) {
        clearActions()
        isVisible = true
        setPosition(stageX - width / 2f, stageY - height / 2f)

        addAction(Actions.forever(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0f, -50f, 0.22f, Interpolation.sineIn),
                    Actions.scaleTo(0.88f, 0.88f, 0.22f, Interpolation.sineIn),
                ),
                Actions.parallel(
                    Actions.moveBy(0f, 50f, 0.30f, Interpolation.sineOut),
                    Actions.scaleTo(1f, 1f, 0.30f, Interpolation.sineOut),
                ),
                Actions.delay(0.5f)
            )
        ))
    }

    // ------------------------------------------------------------------------
    // Drag — Step MERGE
    // ------------------------------------------------------------------------

    fun showDrag(from: Vector2, to: Vector2) {
        clearActions()
        isVisible = true
        setPosition(from.x - width / 2f, from.y - height / 2f)

        addAction(Actions.forever(
            Actions.sequence(
                // стискається — "бере" куб
                Actions.scaleTo(0.88f, 0.88f, 0.15f, Interpolation.sineIn),
                Actions.delay(0.1f),

                // летить до другого куба
                Actions.parallel(
                    Actions.moveTo(to.x - width / 2f, to.y - height / 2f, 0.5f, Interpolation.sineIn),
                    Actions.scaleTo(1f, 1f, 0.2f, Interpolation.sineOut),
                ),

                // "кидає"
                Actions.scaleTo(0.88f, 0.88f, 0.12f, Interpolation.sineIn),
                Actions.scaleTo(1f, 1f, 0.15f, Interpolation.sineOut),
                Actions.delay(0.4f),

                // повертається (невидимо)
                Actions.alpha(0f, 0.15f),
                Actions.moveTo(from.x - width / 2f, from.y - height / 2f, 0f),
                Actions.alpha(1f, 0.15f),
            )
        ))
    }

    // ------------------------------------------------------------------------
    // Hide
    // ------------------------------------------------------------------------

    fun hide() {
        clearActions()
        addAction(Actions.sequence(
            Actions.fadeOut(0.2f),
            Actions.run { isVisible = false }
        ))
    }
}