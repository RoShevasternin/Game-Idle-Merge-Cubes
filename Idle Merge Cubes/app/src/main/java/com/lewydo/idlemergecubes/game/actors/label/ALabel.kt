package com.lewydo.idlemergecubes.game.actors.label

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class ALabel(
    override val screen: AdvancedScreen,
    val text      : String,
    val labelStyle: LabelStyle
) : AdvancedGroup() {

    val label = Label(text, labelStyle)

    override fun getPrefWidth()  = label.prefWidth
    override fun getPrefHeight() = label.prefHeight

    override fun addActorsOnGroup() {
        addAndFillActor(label)
    }

    override fun sizeChanged() {
        super.sizeChanged()
        label.setSize(width, height)
    }

}