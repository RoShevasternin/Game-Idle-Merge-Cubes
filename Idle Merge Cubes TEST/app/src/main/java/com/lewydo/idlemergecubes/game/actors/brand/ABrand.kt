package com.lewydo.idlemergecubes.game.actors.brand

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ABrand(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBrandImg = Image(gdxGame.assetsAll.BRAND)
    private val aBackImg  = Image(gdxGame.assetsAll.BRAND_BACK)
    private val aFrontImg = Image(gdxGame.assetsAll.BRAND_FRONT)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBrandImg)
        addBackImg()
        addFrontImg()

        animHeartbeat()

        setOnClickListener { gdxGame.activity.openPlayMarket() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBackImg() {
        aBackImg.setSize(171f, 171f)
        addActorWithConstraints(aBackImg) {
            topToTopOf = this@ABrand
            endToEndOf = this@ABrand
        }
    }

    private fun addFrontImg() {
        aFrontImg.setSize(115f, 115f)
        addActorWithConstraints(aFrontImg) {
            topToTopOf = this@ABrand
            endToEndOf = this@ABrand
            marginEnd  = 28f
            marginTop  = 28f
        }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animHeartbeat() {
        aFrontImg.setOrigin(Align.center)
        aFrontImg.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.13f, 1.13f, 0.10f, Interpolation.sineOut),
                    Actions.scaleTo(1.00f, 1.00f, 0.12f, Interpolation.sineIn),
                    Actions.scaleTo(1.07f, 1.07f, 0.10f, Interpolation.sineOut),
                    Actions.scaleTo(1.00f, 1.00f, 0.12f, Interpolation.sineIn),
                    Actions.delay(0.65f)
                )
            )
        )

        aBackImg.setOrigin(Align.center)
        aBackImg.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(0.05f),
                    Actions.scaleTo(1.15f, 1.15f, 0.12f, Interpolation.sineOut),
                    Actions.scaleTo(1.05f, 1.05f, 0.28f, Interpolation.sineIn),
                    Actions.delay(0.62f)
                )
            )
        )
    }

}