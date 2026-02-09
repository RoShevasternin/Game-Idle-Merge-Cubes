package com.lewydo.rozval.game.actors.counter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.utils.actor.addActors
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.game.utils.runGDX
import kotlinx.coroutines.launch

class AStarCounter(override val screen: AdvancedScreen, labelStyle: Label.LabelStyle): AdvancedGroup() {

    private val starImg = Image(gdxGame.assetsAll.star_fill)
    private val starLbl = Label(gdxGame.ds_star.flow.value.toString(), labelStyle)

    override fun addActorsOnGroup() {
        addActors(starImg, starLbl)
        starImg.setBounds(0f, 3f, 40f, 40f)
        starLbl.apply {
            setBounds(50f, 0f, 90f, 47f)
            setAlignment(Align.center)
        }

        collectStars()
    }

    // Logic ------------------------------------------------------------------------

    private fun collectStars() {
        coroutine?.launch {
            gdxGame.ds_star.flow.collect { star ->
                runGDX { starLbl.setText(star) }
            }
        }
    }

}