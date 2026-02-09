package com.lewydo.rozval.game.actors.label

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen

class ALabel(
    override val screen: AdvancedScreen,
    text: CharSequence,
    labelStyle: LabelStyle
): AdvancedGroup() {

    val label = Label(text, labelStyle)

    override fun addActorsOnGroup() {
        addActor(label)
        label.setSize(width, height)
    }

}