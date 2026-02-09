package com.lewydo.rozval.game.actors.shader.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.TmpGroup
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.font.FontParameter
import com.lewydo.rozval.util.log

class ABlock(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "%")
    private val font38    = screen.fontGenerator_LondrinaSolid_Regular.generateFont(parameter.setSize(38))

    private val labelStyle38 = Label.LabelStyle(font38, Color.WHITE)

    private val noRotateGroup = TmpGroup(screen)

    private val wood  = AWood(screen)
    private val label = Label((100f - wood.damage).toInt().toString() + "%", labelStyle38)

    override fun addActorsOnGroup() {
        addAndFillActor(wood)
        addLabel()
    }

    override fun act(delta: Float) {
        super.act(delta)
        label.setText((100f - wood.damage).toInt().toString() + "%")

        noRotateGroup.rotation = -rotation
    }

    // Actors ------------------------------------------------------------------------

    private fun addLabel() {
        // ⬇️ КОНТЕЙНЕР ДЛЯ LABEL
        addAndFillActor(noRotateGroup)
        noRotateGroup.addAndFillActor(label)
        noRotateGroup.setOrigin(Align.center)
        //addAndFillActor(label)
        label.setAlignment(Align.center)
    }

    // Logic ------------------------------------------------------------------------

    fun getDamage() = wood.damage

    fun updateDamage(damage: Float) {
        wood.damage = damage
    }

}