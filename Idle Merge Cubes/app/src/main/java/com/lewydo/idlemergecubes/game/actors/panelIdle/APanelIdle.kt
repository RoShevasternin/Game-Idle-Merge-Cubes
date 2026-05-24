package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectActor
import com.lewydo.idlemergecubes.game.utils.IDLE_CYCLE_SECONDS
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.SizeScaler
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animHideAndDisable
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.animShowAndEnable
import com.lewydo.idlemergecubes.game.utils.actor.setPosition
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class APanelIdle(override val screen: AdvancedScreen): AdvancedGroup() {

    private val CONFETTI_PALETTE = arrayOf(
        Color(1.00f, 0.89f, 0.00f, 1f), // #FFE500 жовтий
        Color(1.00f, 0.23f, 0.55f, 1f), // #FF3A8C малиновий
        Color(0.00f, 0.81f, 1.00f, 1f), // #00CFFF блакитний
        Color(0.66f, 0.33f, 0.97f, 1f), // #A855F7 фіолетовий
        Color(0.29f, 0.87f, 0.50f, 1f), // #4ADE80 зелений
        Color(1.00f, 0.48f, 0.00f, 1f), // #FF7A00 помаранчевий
        Color(1.00f, 0.84f, 0.00f, 1f), // #FFD700 золотий
        Color(0.96f, 0.42f, 0.71f, 1f), // #F472B6 рожевий
    )

    private val textTitle = "MERGE BONUS"

    private val BASE_WIDTH_EFFECT = 1047f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter      = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "," + textTitle)
    private val parameterTitle = parameter.copy().setSize(72)
    private val parameterCoins = parameter.copy().setSize(64)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTitleLbl = Label(textTitle, FontFactory.create(screen, parameterTitle, screen.fontGenerator_Nunito_Bold))
    private val aCoinLbl  = Label("0", FontFactory.create(screen, parameterCoins, screen.fontGenerator_Nunito_SemiBold))

    private val aPanelIdleImg = Image(gdxGame.assetsAll.PANEL_IDLE)
    private val aCoinImg      = Image(gdxGame.assetsAll.coin)

    private val aPanelProgressIdle = APanelProgressIdle(screen)
    private val aPanelCollectIdle  = APanelCollectIdle(screen)

    private val listConfettiEffect = List(8) { AParticleEffectActor(ParticleEffect(gdxGame.particleEffectAll.IDLE_CONFETTI)) }
    private val aWaveEffect        = AParticleEffectActor(gdxGame.particleEffectAll.IDLE_WAVE)

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

        addEffectConfetti()
        addEffectWave()

        collectMergeBonusProgress()
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
        aPanelCollectIdle.animHideAndDisable()
        addAndFillActor(aPanelCollectIdle)

        aPanelCollectIdle.apply {
            onCollect = {
                gdxGame.modelMergeBonus.collect()
                stateFlow.value = IdlePanelState.FILLING
            }
            onCollectX2 = {
                gdxGame.modelMergeBonus.collectX2()
                stateFlow.value = IdlePanelState.FILLING
            }
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors - Effect
    // ------------------------------------------------------------------------

    private fun addEffectConfetti() {
        listConfettiEffect.forEachIndexed { index, aEffect ->
            addActor(aEffect)
            aEffect.setPosition(getListConfettiPos()[index])

            aEffect.fitToSize(targetWidth = width, baseWidth = BASE_WIDTH_EFFECT)
            aEffect.setColorPalette(*CONFETTI_PALETTE)
        }
    }

    private fun addEffectWave() {
        addActorAligned(aWaveEffect, AlignH.CENTER, AlignV.CENTER)
        aWaveEffect.fitToSize(targetWidth = width, baseWidth = BASE_WIDTH_EFFECT)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectMergeBonusProgress() {
        coroutine?.launch {
            gdxGame.modelMergeBonus.rewardFlow.collect { reward ->
                runGDX {
                    aCoinLbl.setText(NumberFormatter.format(reward))
                    aPanelCollectIdle.setReward(reward)
                }
            }
        }

        coroutine?.launch {
            gdxGame.modelMergeBonus.progressFlow.collect { progress ->
                runGDX {
                    val count = gdxGame.modelMergeBonus.countFlow.value
                    val goal  = gdxGame.modelMergeBonus.goalFlow.value
                    aPanelProgressIdle.updateProgress(count, goal)

                    if (progress >= 1f && stateFlow.value == IdlePanelState.FILLING) {
                        stateFlow.value = IdlePanelState.READY
                    }
                }
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
                aPanelCollectIdle.animHideAndDisable(0.25f)

                //aPanelProgressIdle.startIdleCycle(IDLE_CYCLE_SECONDS)
            }

            IdlePanelState.READY -> {
                aPanelProgressIdle.animHide(0.25f)
                aPanelCollectIdle.animShowAndEnable(0.25f)

                listConfettiEffect.forEach { it.start() }
                aWaveEffect.start()

                gdxGame.soundUtil.apply { play(SHOW_COLLECT) }
            }
        }
    }

    // ------------------------------------------------------------------------
    // enum State
    // ------------------------------------------------------------------------

    private fun getListConfettiPos(): List<Vector2> {
        val sizeScaler = SizeScaler(SizeScaler.Axis.X, BASE_WIDTH_EFFECT)
        sizeScaler.calculateScale(Vector2(width, 0f))


        return listOf(
            sizeScaler.toActual(Vector2(65f, 133f) ),
            sizeScaler.toActual(Vector2(261f, 133f)),
            sizeScaler.toActual(Vector2(524f, 117f)), // CENTER | index = 2
            sizeScaler.toActual(Vector2(787f, 133f)),
            sizeScaler.toActual(Vector2(983f, 133f)),
            sizeScaler.toActual(Vector2(163f, 210f)),
            sizeScaler.toActual(Vector2(524f, 235f)),
            sizeScaler.toActual(Vector2(885f, 210f)),
        )
    }

    enum class IdlePanelState {
        FILLING,
        READY
    }

}