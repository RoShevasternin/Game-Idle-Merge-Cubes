package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.graphics.Color
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
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelLvL(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "Level")
    private val fontLvL   = screen.fontGenerator_Nunito_ExtraBold.generateFont(parameter.setSize(87).setShadow(8, 7, GameColor.purple_350080))
    private val fontLevel = screen.fontGenerator_Nunito_Regular.generateFont(parameter.setSize(45).setShadow(5, 7, GameColor.purple_350080))

    private val aPanelLvLImg = Image(gdxGame.assetsAll.panel_lvl)
    private val aLvLLbl      = Label("1", Label.LabelStyle(fontLvL, Color.WHITE))
    private val aLevelLbl    = Label("Level", Label.LabelStyle(fontLevel, Color.WHITE))

    private val aCircleProgress = ACircleProgress(screen, -0f, 0f, 90f)
    private val aLvLPopup       = ALevelPopup(screen)

    // Field
    private var isVisiblePopup = false

    override fun addActorsOnGroup() {
        addPanelLvLImg()
        addLvLLbl()
        addLevelLbl()
        addCircleProgress()

        addLvLPopup()

        collectPlayerData()
        handleClick()
    }

    // Actors ------------------------------------------------------------------------

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

    private fun addLvLPopup() {
        addActor(aLvLPopup)
        aLvLPopup.setBounds(30f, -290f, 699f, 320f)

        aLvLPopup.apply {
            disable()
            setOrigin(Align.topLeft)
            setScale(0.8f)
            color.a = 0f
        }
    }

    private fun addCircleProgress() {
        addActor(aCircleProgress)
        aCircleProgress.setBounds(0f, 0f, 270f, 270f)
    }

    // Anim ------------------------------------------------------------------------

    private fun animShowPopup() {
        isVisiblePopup = true
        aLvLPopup.clearActions()
        aLvLPopup.addAction(
            Actions.parallel(
                Actions.fadeIn(0.25f),
                Actions.scaleTo(1f, 1f, 0.25f)
            )
        )
    }

    private fun animHidePopup() {
        isVisiblePopup = false
        aLvLPopup.clearActions()
        aLvLPopup.addAction(
            Actions.parallel(
                Actions.fadeOut(0.2f),
                Actions.scaleTo(0.8f, 0.8f, 0.2f)
            ),
        )
    }

    // Logic ------------------------------------------------------------------------

    private fun handleClick() {
        setOnClickListener {
            if (isVisiblePopup) animHidePopup() else animShowPopup()
        }
    }

    private fun collectPlayerData() {
        coroutine?.launch {
            launch { gdxGame.modelPlayer.levelFlow.collect { lvl -> runGDX { aLvLLbl.setText(lvl) } } }
            launch { gdxGame.modelPlayer.xpFlow.collect { runGDX { aCircleProgress.setProgress(-gdxGame.modelPlayer.progressPercent100()) } } }
        }
    }

}