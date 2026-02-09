package com.lewydo.rozval.game.actors.button.location.button

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.label.ALabel
import com.lewydo.rozval.game.actors.shader.ASaturationImage
import com.lewydo.rozval.game.utils.actor.disable
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.gdxGame

abstract class AbstractLevelButton: AdvancedGroup() {

    abstract val level     : Int
    abstract val labelStyle: Label.LabelStyle

    // Actors
    private val previewImg by lazy { ASaturationImage(screen, gdxGame.assetsAll.list_PreviewLvl[level-1]) }
    private val lvlLbl     by lazy { ALabel(screen, level.toString(), labelStyle) }

    override fun addActorsOnGroup() {
        addPreview()
        addLvlLbl()
    }

    // Actors ------------------------------------------------------------------------

    private fun addPreview() {
        addActor(previewImg)
        previewImg.setBounds(13f, 64f, 223f, 223f)
        previewImg.disable()
    }

    private fun addLvlLbl() {
        addActor(lvlLbl)
        lvlLbl.disable()
        lvlLbl.apply {
            setBounds(0f, 242f, 58f, 58f)
            label.setAlignment(Align.center)
            setOrigin(Align.center)
            rotation = 45f
        }
    }

    // Logic ------------------------------------------------------------------------

    fun setSaturationPreview(value: Float) {
        previewImg.saturation = value
    }

}