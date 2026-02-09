package com.lewydo.rozval.game.actors.counter

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.utils.actor.addActors
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.game.utils.runGDX
import kotlinx.coroutines.launch

class AKeyCounter(override val screen: AdvancedScreen, labelStyle: Label.LabelStyle): AdvancedGroup() {

    private val keyImg = Image(gdxGame.assetsAll.key_fill)
    private val keyLbl = Label(gdxGame.ds_key.flow.value.toString(), labelStyle)

    override fun addActorsOnGroup() {
        addActors(keyImg, keyLbl)
        keyImg.setBounds(0f, 3f, 40f, 40f)
        keyLbl.apply {
            setBounds(50f, 0f, 90f, 47f)
            setAlignment(Align.center)
        }

        collectKeys()
    }

    // Logic ------------------------------------------------------------------------

    private fun collectKeys() {
        coroutine?.launch {
            gdxGame.ds_key.flow.collect { key ->
                runGDX { keyLbl.setText(key) }
            }
        }
    }

}