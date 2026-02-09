package com.lewydo.rozval.game.actors.button.location.button

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.rozval.game.actors.button.AButton
import com.lewydo.rozval.game.actors.button.location.actors.AStarOrKey
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.actor.disable
import com.lewydo.rozval.game.utils.actor.enable
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.util.log

class ALockStarKeyLevelButton(
    override val screen    : AdvancedScreen,
    override val level     : Int,
    override val labelStyle: Label.LabelStyle,
    necessaryStars   : Int,
    val keyCount     : Int,
    val necessaryKeys: Int,
) : AbstractLevelButton() {

    // Actors
    private val clickActor = Actor()
    private val button     = AButton(screen, AButton.Type.LevelOpenOfKey)
    private val lockBtn    = AButton(screen, AButton.Type.Lock)
    private val starOrKey  = AStarOrKey(screen, labelStyle, necessaryStars, keyCount, necessaryKeys)

    private val clickableList = listOf(button, lockBtn)

    var onClickBlock: () -> Unit = {}

    override fun addActorsOnGroup() {
        setSaturationPreview(0f)

        clickableList.onEach { it.disable(false) }
        clickActor.disable()

        addButton()
        super.addActorsOnGroup()

        addLockBtn()
        addStarOrKey()
        addClickActor()

        if (keyCount >= necessaryKeys) enableOfKey()
    }

    // Actors ------------------------------------------------------------------------

    private fun addButton() {
        addAndFillActor(button)
    }

    private fun addLockBtn() {
        addActor(lockBtn)
        lockBtn.setBounds(55f, 105f, 140f, 140f)
    }

    private fun addStarOrKey() {
        addActor(starOrKey)
        starOrKey.setBounds(13f, 17f, 223f, 30f)
    }

    private fun addClickActor() {
        addAndFillActor(clickActor)
        clickActor.addListener(getClickInputListener())
    }

    // Logic ------------------------------------------------------------------------

    private fun getClickInputListener() = object : InputListener() {
        var isWithin     = false

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDragged(event, x, y, pointer)
            gdxGame.soundUtil.apply { play(click) }
            setSaturationPreview(1f)

            event?.stop()
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            isWithin = x in 0f..width && y in 0f..height
            if (isWithin) {
                setSaturationPreview(1f)
                clickableList.onEach { it.press() }
            } else {
                setSaturationPreview(0f)
                clickableList.onEach { it.unpress() }
            }
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, b: Int) {
            if (isWithin) {
                onClickBlock()
                setSaturationPreview(0f)
                clickableList.onEach { it.unpress() }
            }
        }
    }

    private fun enableOfKey() {
        clickActor.enable()
        starOrKey.enableKey()
    }

    fun disableOfKey() {
        clickActor.disable()
        starOrKey.disableKey()
    }

    fun changeToAvailableLevelButton() {
        val availableLevelButton = AAvailableLevelButton(screen, level, labelStyle)

        animHide(0.25f) {
            disposeAndClearChildren()
            addAndFillActor(availableLevelButton)
            animShow(0.25f)
        }
    }


}