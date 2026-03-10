package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.utils.IDLE_CYCLE_SECONDS
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class APanelIdle(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textIdleIncome = "IDLE INCOME"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "," + textIdleIncome)
    private val fontTitle = screen.fontGenerator_Nunito_Bold.generateFont(parameter.setSize(72))
    private val fontCoins = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(64))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTitleLbl = Label(textIdleIncome, Label.LabelStyle(fontTitle, Color.WHITE))
    private val aCoinLbl  = Label("0", Label.LabelStyle(fontCoins, Color.WHITE))

    private val aPanelIdleImg = Image(gdxGame.assetsAll.PANEL_IDLE)
    private val aCoinImg      = Image(gdxGame.assetsAll.coin)

    private val aPanelProgressIdle = APanelProgressIdle(screen)
    private val aPanelCollectIdle  = APanelCollectIdle(screen)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------

    private val stateFlow = MutableStateFlow(IdlePanelState.FILLING)

    val currentState
        get() = stateFlow.value

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelIdleImg()
        addTitleLbl()
        addCoinsLbl()
        addCoinImg()

        addPanelProgressIdle()
        addPanelCollectIdle()

        collectIdleReward()
        collectState()

    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanelIdleImg() {
        addAndFillActor(aPanelIdleImg)
    }

    private fun addTitleLbl() {
        addActor(aTitleLbl)
        aTitleLbl.setBounds(48f, 269f, 462f, 98f)
    }

    private fun addCoinsLbl() {
        addActor(aCoinLbl)
        aCoinLbl.setBounds(1742f, 275f, 39f, 87f)
        aCoinLbl.setAlignment(Align.right)
    }

    private fun addCoinImg() {
        addActor(aCoinImg)
        aCoinImg.setBounds(1793f, 288f, 60f, 60f)
    }

    private fun addPanelProgressIdle() {
        addAndFillActor(aPanelProgressIdle)

        aPanelProgressIdle.onFinished = {
            stateFlow.value = IdlePanelState.READY
        }
    }

    private fun addPanelCollectIdle() {
        aPanelCollectIdle.color.a = 0f
        addAndFillActor(aPanelCollectIdle)

        aPanelCollectIdle.apply {
            onCollect = {
                gdxGame.modelIdle.collect()
                stateFlow.value = IdlePanelState.FILLING
            }
            onCollectX2 = {
                gdxGame.modelIdle.collectX2()
                stateFlow.value = IdlePanelState.FILLING
            }
        }
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectIdleReward() {
        coroutine?.launch {
            gdxGame.modelIdle.rewardFlow.collect { reward ->
                runGDX { aCoinLbl.setText(NumberFormatter.format(reward)) }
            }
        }
    }

    private fun collectState() {
        coroutine?.launch {
            stateFlow.collect { state ->
                runGDX { applyState(state) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun applyState(state: IdlePanelState) {
        when (state) {
            IdlePanelState.FILLING -> {
                aPanelProgressIdle.animShow(0.25f)
                aPanelCollectIdle.animHide(0.25f)

                aPanelProgressIdle.startIdleCycle(IDLE_CYCLE_SECONDS)
            }

            IdlePanelState.READY -> {
                aPanelProgressIdle.animHide(0.25f)
                aPanelCollectIdle.animShow(0.25f)
            }
        }
    }

    // ------------------------------------------------------------------------
    // enum State
    // ------------------------------------------------------------------------

    enum class IdlePanelState {
        FILLING,
        READY
    }

}