package com.lewydo.idlemergecubes.game.actors.tutorial

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ATutorialHand(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Offset
    // ------------------------------------------------------------------------

    private val offsetX = 28f
    private val offsetY = 445f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aHandImg = Image(gdxGame.assetsAll.tutorial_hand)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        setOrigin(offsetX, offsetY)
        addAndFillActor(aHandImg)
        isVisible = false
    }

    // ------------------------------------------------------------------------
    // Tap — Step BUY
    // ------------------------------------------------------------------------

    fun showTap(stageX: Float, stageY: Float) {
        clearActions()
        isVisible = true

        setPosition(stageX - offsetX, stageY - offsetY)

        setScale(0.6f)
        y       -= 200f

        addAction(Actions.sequence(
            // --- поява ---
            Actions.parallel(
                Actions.fadeIn(0.35f, Interpolation.sineOut),
                Actions.moveBy(0f, 200f, 0.45f, Interpolation.swingOut),
                Actions.scaleTo(1.1f, 1.1f, 0.35f, Interpolation.swingOut),
            ),
            Actions.scaleTo(1f, 1f, 0.15f, Interpolation.sineOut),
            Actions.delay(0.3f),

            // --- тап петля ---
            Actions.forever(
                Actions.sequence(
                    // опускається — натискає
                    Actions.parallel(
                        Actions.moveBy(0f, -55f, 0.18f, Interpolation.sineIn),
                        Actions.scaleTo(0.90f, 0.90f, 0.18f, Interpolation.sineIn),
                    ),
                    // піднімається назад
                    Actions.parallel(
                        Actions.moveBy(0f, 55f, 0.28f, Interpolation.swingOut),
                        Actions.scaleTo(1f, 1f, 0.28f, Interpolation.swingOut),
                    ),
                    Actions.delay(0.55f)
                )
            )
        ))
    }

    // ------------------------------------------------------------------------
    // Drag — Step MERGE
    // ------------------------------------------------------------------------

    fun showDrag(from: Vector2, to: Vector2) {
        clearActions()
        isVisible = true

        setPosition(from.x - offsetX, from.y - offsetY)
        color.a = 0f
        setScale(0.6f)
        y -= 200f

        addAction(Actions.sequence(
            // --- поява (як showTap) ---
            Actions.parallel(
                Actions.fadeIn(0.35f, Interpolation.sineOut),
                Actions.moveBy(0f, 200f, 0.45f, Interpolation.swingOut),
                Actions.scaleTo(1.1f, 1.1f, 0.35f, Interpolation.swingOut),
            ),
            Actions.scaleTo(1f, 1f, 0.15f, Interpolation.sineOut),
            Actions.delay(0.4f),

            // --- drag петля ---
            Actions.forever(
                Actions.sequence(
                    // стискається — "бере" куб
                    Actions.scaleTo(0.85f, 0.85f, 0.18f, Interpolation.sineIn),
                    Actions.delay(0.15f),

                    // летить до цілі
                    Actions.parallel(
                        Actions.moveTo(to.x - offsetX, to.y - offsetY, 0.55f, Interpolation.sineOut),
                        Actions.scaleTo(0.95f, 0.95f, 0.55f, Interpolation.sineOut),
                    ),

                    // "відпускає" — легкий стрибок
                    Actions.scaleTo(1.1f, 1.1f, 0.1f, Interpolation.sineOut),
                    Actions.scaleTo(1f, 1f, 0.15f, Interpolation.swingOut),
                    Actions.delay(0.35f),

                    // невидимо повертається на старт
                    Actions.parallel(
                        Actions.alpha(0f, 0.18f, Interpolation.sineIn),
                        Actions.scaleTo(0.8f, 0.8f, 0.18f, Interpolation.sineIn),
                    ),
                    Actions.moveTo(from.x - offsetX, from.y - offsetY, 0f),
                    Actions.parallel(
                        Actions.alpha(1f, 0.2f, Interpolation.sineOut),
                        Actions.scaleTo(1f, 1f, 0.2f, Interpolation.swingOut),
                    ),
                    Actions.delay(0.2f),
                )
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