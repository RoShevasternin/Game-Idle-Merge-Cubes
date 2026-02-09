package com.lewydo.rozval.game.actors.button.location.actors

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame

class AStar(override val screen: AdvancedScreen): AdvancedGroup() {

    private val assetsAll = gdxGame.assetsAll

    private val image = Image(assetsAll.star_empty)

    override fun addActorsOnGroup() {
        addAndFillActor(image)
    }

    fun fill() {
        image.drawable = TextureRegionDrawable(assetsAll.star_fill)
    }

}