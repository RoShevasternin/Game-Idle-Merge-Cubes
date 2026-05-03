package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.label.ALabel
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontGenerator
import com.lewydo.idlemergecubes.game.utils.font.FontParameter

// ATextButton.kt — тепер наслідується від AButtonTexture
open class ATextButton(
    override val screen: AdvancedScreen,
    text     : String,
    color    : Color,
    parameter: FontParameter,
    generator: FontGenerator,
    style    : Style = AButtonStyles.NONE,
) : AButtonTexture(screen, style) {

    val label = ALabel(screen, text, color, parameter, generator)

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addAndFillActor(label)
        label.disable()
        label.setAlignment(Align.center)
    }
}