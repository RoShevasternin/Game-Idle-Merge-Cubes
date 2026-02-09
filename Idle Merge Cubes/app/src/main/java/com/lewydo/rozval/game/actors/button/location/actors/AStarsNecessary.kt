package com.lewydo.rozval.game.actors.button.location.actors

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.autoLayout.AHorizontalGroup
import com.lewydo.rozval.game.actors.autoLayout.AutoLayout
import com.lewydo.rozval.game.utils.SizeScaler
import com.lewydo.rozval.game.utils.actor.addActors
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.util.log

class AStarsNecessary(
    override val screen: AdvancedScreen,
    starCount : Int,
    labelStyle: LabelStyle,
): AHorizontalGroup(
    screen,
    alignmentH = AutoLayout.AlignmentHorizontal.CENTER,
    alignmentV = AutoLayout.AlignmentVertical.CENTER,
    isWrapH = true,
) {

    override val sizeScaler = SizeScaler(SizeScaler.Axis.Y, 30f)

    private val assetsAll = gdxGame.assetsAll

    private val starImg      = Image(assetsAll.star_grey)
    private val starCountLbl = Label(starCount.toString(), labelStyle)

    override fun addActorsOnGroup() {
        gap = 7f.toActual
        addStars()
    }

    // Actors ------------------------------------------------------------------------

    private fun addStars() {
        starImg.setSizeScaled(30f, 30f)

        starCountLbl.height = 30f.toActual
        starCountLbl.setAlignment(Align.center)

        addActors(starImg, starCountLbl)
    }

}