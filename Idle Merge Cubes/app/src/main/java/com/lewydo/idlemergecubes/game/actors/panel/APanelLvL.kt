package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.popup.ALevelPopup
import com.lewydo.idlemergecubes.game.actors.progress.ACircleProgress
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class APanelLvL(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter      = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "Level")
    private val parameterLvL   = parameter.copy().setSize(87).setShadow(8, 7, GameColor.purple_350080)
    private val parameterLevel = parameter.copy().setSize(45).setShadow(5, 7, GameColor.purple_350080)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelLvLImg    = Image(gdxGame.assetsAll.panel_lvl)
    private val aLvLLbl         = Label("1", FontFactory.create(screen, parameterLvL, screen.fontGenerator_Nunito_ExtraBold))
    private val aLevelLbl       = Label("Level", FontFactory.create(screen, parameterLevel, screen.fontGenerator_Nunito_Regular))
    private val aCircleProgress = ACircleProgress(screen, 0f, 0f, 90f)
    private val aLvLPopup       = ALevelPopup(screen)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------

    private var isPopupVisible = false

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        setOrigin(Align.center)

        addPanelLvLImg()
        addLvLLbl()
        addLevelLbl()
        addCircleProgress()
        addLvLPopup()

        collectLevel()
        collectXp()
        collectLayoutComplete()
        collectShakeEvent()
        handleClick()
    }

    // Реєструємо позицію коли наш батько APanelTop змінює нашу локальну позицію
    override fun positionChanged() {
        super.positionChanged()
        if (stage != null) registerStagePosition()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanelLvLImg() {
        addActor(aPanelLvLImg)
        aPanelLvLImg.setBounds(18f, 18f, 235f, 235f)
    }

    private fun addLvLLbl() {
        addActor(aLvLLbl)
        aLvLLbl.setBounds(110f, 101f, 53f, 119f)
        aLvLLbl.setAlignment(Align.center)
    }

    private fun addLevelLbl() {
        addActor(aLevelLbl)
        aLevelLbl.setBounds(82f, 68f, 109f, 61f)
        aLevelLbl.setAlignment(Align.center)
    }

    private fun addCircleProgress() {
        addActor(aCircleProgress)
        aCircleProgress.setBounds(0f, 0f, 270f, 270f)
    }

    private fun addLvLPopup() {
        addActor(aLvLPopup)
        aLvLPopup.setBounds(30f, -290f, 699f, 320f)
        aLvLPopup.disable()
        aLvLPopup.setOrigin(Align.topLeft)
        aLvLPopup.setScale(0.8f)
        aLvLPopup.color.a = 0f
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectLevel() {
        coroutine?.launch {
            gdxGame.modelPlayer.levelFlow.collect { level ->
                runGDX { aLvLLbl.setText(level.toString()) }
            }
        }
    }

    private fun collectXp() {
        coroutine?.launch {
            gdxGame.modelPlayer.xpFlow.collect {
                runGDX { aCircleProgress.setProgress(gdxGame.modelPlayer.levelProgress() * 100f) }
            }
        }
    }

    // Після resize → AConstraintLayout.layout() → позиції оновились → перереєстровуємо
    private fun collectLayoutComplete() {
        coroutine?.launch {
            GlobalEvents.events
                .filter { it == GlobalEvents.EventType.CONSTRAINT_LAYOUT_COMPLETE }
                .collect { runGDX { registerStagePosition() } }
        }
    }

    private fun collectShakeEvent() {
        coroutine?.launch {
            GlobalEvents.events
                .filter { it == GlobalEvents.EventType.END_FLY_XP }
                .collect { runGDX { animShake() } }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun handleClick() {
        setOnClickListener {
            if (isPopupVisible) animHidePopup() else animShowPopup()
        }
    }

    private fun registerStagePosition() {
        val pos = aPanelLvLImg.localToStageCoordinates(
            Vector2(aPanelLvLImg.width / 2f, aPanelLvLImg.height / 2f)
        )
        GlobalStagePositions.register(GlobalStagePositions.Position.XP, pos.x, pos.y)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animShowPopup() {
        isPopupVisible = true
        aLvLPopup.clearActions()
        aLvLPopup.addAction(
            Actions.parallel(
                Actions.fadeIn(0.25f),
                Actions.scaleTo(1f, 1f, 0.25f)
            )
        )
    }

    private fun animHidePopup() {
        isPopupVisible = false
        aLvLPopup.clearActions()
        aLvLPopup.addAction(
            Actions.parallel(
                Actions.fadeOut(0.2f),
                Actions.scaleTo(0.8f, 0.8f, 0.2f)
            )
        )
    }

    private fun animShake() {
        clearActions()
        addAction(
            Actions.sequence(
                Actions.scaleTo(1.08f, 1.08f, 0.1f, Interpolation.sineOut),
                Actions.scaleTo(1.0f,  1.0f,  0.2f, Interpolation.sineOut)
            )
        )
    }
}