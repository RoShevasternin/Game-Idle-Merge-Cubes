package com.lewydo.idlemergecubes.game.actors.label

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AFlyingLabel(
    override val screen: AdvancedScreen,
    text      : String,
    val style : Label.LabelStyle,
    val to    : Vector2,
    val type  : Type,
    val onEnd : Block,
) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Type
    // ------------------------------------------------------------------------

    enum class Type { COIN, XP }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val label   = Label(text, style)
    private val coinImg = if (type == Type.COIN) Image(gdxGame.assetsAll.coin) else null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        val layout      = GlyphLayout(style.font, label.text)
        val labelWidth  = layout.width
        val labelHeight = layout.height
        val iconSize    = labelHeight * 1.2f
        val gap         = 8f

        when (type) {
            Type.COIN -> {
                coinImg?.let {
                    addActor(it)
                    // іконка по центру висоти labelHeight
                    val iconY = (labelHeight - iconSize)
                    it.setBounds(0f, iconY, iconSize, iconSize)
                }
                addActor(label)
                label.setBounds(iconSize + gap, 0f, labelWidth, labelHeight)
                setSize(iconSize + gap + labelWidth, labelHeight)
            }
            Type.XP -> {
                addActor(label)
                label.setBounds(0f, 0f, labelWidth, labelHeight)
                setSize(labelWidth, labelHeight)
            }
        }

        setScale(0f)
        startFly()
    }

    // ------------------------------------------------------------------------
    // Fly
    // ------------------------------------------------------------------------

    private fun startFly() {
        val gap     = 25f
        val offsetX = if (type == Type.COIN) -(width + gap) else gap
        val floatX  = x + offsetX
        val floatY  = y + 200f

        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.15f, Interpolation.swingOut),
                Actions.moveTo(floatX, floatY, 0.25f, Interpolation.swingOut),
            ),
            Actions.delay(0.2f),
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