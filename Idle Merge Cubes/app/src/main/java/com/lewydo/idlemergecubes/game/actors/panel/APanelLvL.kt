package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.popup.ALevelPopup
import com.lewydo.idlemergecubes.game.actors.progress.ACircleProgress
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfLabel
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class APanelLvL(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleLvL = MsdfStyle(msdf, msdf.fontNunitoExtraBold, 87f)
        .dropShadow(8f, 7f, 4f, GameColor.purple_350080)
    private val styleLevel = MsdfStyle(msdf, msdf.fontNunitoRegular, 45f)
        .dropShadow(5f, 7f, 4f, GameColor.purple_350080)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelLvLImg    = Image(gdxGame.assetsAll.panel_lvl)
    private val aLvLLbl         = MsdfLabel("1", styleLvL)
    private val aLevelLbl       = MsdfLabel("Level", styleLevel)
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
        collectShakeEvent()
        handleClick()

        registerStagePosition()
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
        aLvLLbl.setSize(53f, 119f)
        add(aLvLLbl) { centerX(); topToTop(margin = 48f) }
        aLvLLbl.setAlignment(Align.center)
    }

    private fun addLevelLbl() {
        aLevelLbl.setSize(109f, 61f)
        add(aLevelLbl) { centerX(); topToBottom(aLvLLbl, -28f) }
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
        GlobalStagePositions.register(
            key      = GlobalStagePositions.Key.XP,
            actor    = aPanelLvLImg,
            offsetX  = aPanelLvLImg.width  / 2f,
            offsetY  = aPanelLvLImg.height / 2f,
        )
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