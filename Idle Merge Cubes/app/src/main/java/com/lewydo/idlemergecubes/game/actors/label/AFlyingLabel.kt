package com.lewydo.idlemergecubes.game.actors.label

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class AFlyingLabel(
    override val screen: AdvancedScreen,
    text      : String,
    val style : Label.LabelStyle,
    val to    : Vector2,
    val side  : Side,
    val onEnd : Block,
) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Side
    // ------------------------------------------------------------------------

    enum class Side { Left, Right }

    private val label = Label(text, style)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addAndFillActor(label)
        setScale(0f)
        startFly()
    }

    // ------------------------------------------------------------------------
    // Fly
    // ------------------------------------------------------------------------

    private fun startFly() {
        // Рахуємо реальну ширину тексту
        val layout     = GlyphLayout(style.font, label.text)
        val labelWidth = layout.width

        val gap       = 25f  // половина відступу між лейблами
        val offsetX   = if (side == Side.Left) -(labelWidth + gap) else gap

        // фаза 1 — підлітаємо вгору і вбік від куба
        val floatX = x + offsetX
        val floatY = y + 200f

        addAction(Actions.sequence(
            Actions.parallel(
            // поява
            Actions.scaleTo(1f, 1f, 0.15f, Interpolation.swingOut),
            // підлітаємо до float позиції
            Actions.moveTo(floatX, floatY, 0.25f, Interpolation.swingOut),
            ),

            // зависаємо
            Actions.delay(0.2f),

            // летимо до цілі
            Actions.parallel(
                Actions.moveTo(to.x, to.y, 0.45f, Interpolation.sineIn),
                Actions.sequence(
                    Actions.scaleTo(1.1f, 1.1f, 0.15f, Interpolation.sine),
                    Actions.scaleTo(0.25f, 0.25f, 0.30f, Interpolation.sineIn),
                ),
                Actions.alpha(0.5f, 0.45f),
            ),
            Actions.run {
                onEnd()
                remove()
            }
        ))
    }

}