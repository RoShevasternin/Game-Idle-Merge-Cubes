package com.lewydo.rozval.game.actors.progress

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.mask.AOldMask
import com.lewydo.rozval.game.utils.WIDTH_UI
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.disable
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.font.FontParameter
import com.lewydo.rozval.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AProgressDefault(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "%")
    private val font80    = screen.fontGenerator_LondrinaSolid_Regular.generateFont(parameter.setSize(38))

    private val labelStyle80 = LabelStyle(font80, Color.valueOf("A82800"))

    private val LENGTH = 698f

    private val backgroundImage = Image(screen.drawerUtil.getRegion(Color.WHITE))
    private val progressImage   = Image(screen.drawerUtil.getRegion(Color.valueOf("86B216")))
    private val mask            = AOldMask(screen, alphaWidth = WIDTH_UI.toInt())

    private val label = Label("", labelStyle80)

    private val onePercentX = LENGTH / 100f

    // 0 .. 100 %
    val progressPercentFlow = MutableStateFlow(0f)


    override fun addActorsOnGroup() {
        addBackground()
        addMask()
        addLabel()

        coroutine?.launch {
            progressPercentFlow.collect { percent ->
                runGDX {
                    progressImage.x = (percent * onePercentX) - LENGTH
                    label.setText("${percent.toInt()}%")
                }
            }
        }

        addListener(inputListener())
    }

    // ---------------------------------------------------
    // Add Actors
    // ---------------------------------------------------

    private fun AdvancedGroup.addBackground() {
        addAndFillActor(backgroundImage)
    }

    private fun AdvancedGroup.addMask() {
        addAndFillActor(mask)
        mask.addProgress()
    }

    private fun AdvancedGroup.addProgress() {
        addAndFillActor(progressImage)
    }

    private fun AdvancedGroup.addLabel() {
        addAndFillActor(label)
        label.setAlignment(Align.center)
        label.disable()
    }

    // ---------------------------------------------------
    // Logic
    // ---------------------------------------------------

    private fun inputListener() = object : InputListener() {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDragged(event, x, y, pointer)
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            progressPercentFlow.value = when {
                x <= 0 -> 0f
                x >= LENGTH -> 100f
                else -> x / onePercentX
            }

            event?.stop()
        }
    }

}