package com.lewydo.rozval.game.actors.button.location.separator

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.autoLayout.AHorizontalGroup
import com.lewydo.rozval.game.actors.autoLayout.AutoLayout
import com.lewydo.rozval.game.actors.button.AButton
import com.lewydo.rozval.game.actors.button.location.actors.AKeysNecessary
import com.lewydo.rozval.game.actors.button.location.actors.AStarsNecessary
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.addAndFillActors
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.actor.disable
import com.lewydo.rozval.game.utils.actor.enable
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.util.log

class ALockSeparatorButton(
    override val screen    : AdvancedScreen,
    override val title     : String,
    override val labelStyle: LabelStyle,
    necessaryStars: Int,
    private val keyCount : Int,
    val necessaryKeys: Int,
) : AbstractSeparatorButton() {

    // Actors
    private val clickActor = Actor()

    private val horizontalGroup = AHorizontalGroup(screen, 40f,
        alignmentH = AutoLayout.AlignmentHorizontal.CENTER,
        alignmentV = AutoLayout.AlignmentVertical.CENTER,
    )
    private val separatorBtn  = AButton(screen, AButton.Type.SeparatorOpenOfKey)
    private val lockBtn       = AButton(screen, AButton.Type.Lock)
    private val titleLbl      = Label(title, labelStyle)
    private val starNecessary = AStarsNecessary(screen, necessaryStars, labelStyle)
    private val orLbl         = Label("or", labelStyle)
    private val keyNecessary  = AKeysNecessary(screen, keyCount, necessaryKeys, labelStyle)

    private val clickableList = listOf(separatorBtn, lockBtn)

    var onClickBlock: () -> Unit = {}

    override fun addActorsOnGroup() {
        clickableList.onEach { it.disable(false) }
        clickActor.disable()

        addAndFillActors(separatorBtn, horizontalGroup)

        horizontalGroup.apply {
            addLockBtn()
            addTitleLbl()
            addStarNecessary()
            addOrLabel()
            addKeyNecessary()
        }

        addClickActor()

        if (keyCount >= necessaryKeys) enableOfKey()

    }

    // Actors ------------------------------------------------------------------------

    private fun AHorizontalGroup.addLockBtn() {
        lockBtn.setSize(80f, 80f)
        addActor(lockBtn)
    }

    private fun AHorizontalGroup.addTitleLbl() {
        titleLbl.height = 52f
        titleLbl.setAlignment(Align.center)
        addActor(titleLbl)
    }

    private fun AHorizontalGroup.addStarNecessary() {
        starNecessary.height = 52f
        starNecessary.width  = 1f
        addActor(starNecessary)
    }

    private fun AdvancedGroup.addOrLabel() {
        orLbl.height = 52f
        orLbl.setAlignment(Align.center)
        addActor(orLbl)
    }

    private fun AHorizontalGroup.addKeyNecessary() {
        keyNecessary.height = 52f
        keyNecessary.width  = 1f
        addActor(keyNecessary)
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

            event?.stop()
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            isWithin = x in 0f..width && y in 0f..height
            if (isWithin) {
                clickableList.onEach { it.press() }
            } else {
                clickableList.onEach { it.unpress() }
            }
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, b: Int) {
            if (isWithin) {
                onClickBlock()
                clickableList.onEach { it.unpress() }
            }
        }
    }

    private fun enableOfKey() {
        clickActor.enable()
        keyNecessary.enableKey()
    }

    fun disableOfKey() {
        clickActor.disable()
        keyNecessary.disableKey()
    }

    fun changeToAvailableSeparatorButton() {
        val availableSeparatorButton = AAvailableSeparatorButton(screen, title, labelStyle)

        animHide(0.25f) {
            disposeAndClearChildren()
            addAndFillActor(availableSeparatorButton)
            animShow(0.25f)
        }
    }

}