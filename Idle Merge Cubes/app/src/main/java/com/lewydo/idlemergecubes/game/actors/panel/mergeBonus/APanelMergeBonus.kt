package com.lewydo.idlemergecubes.game.actors.panel.mergeBonus

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.state.FillingState
import com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.state.ReadyState
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectActor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.SizeScaler
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.animHideAndDisable
import com.lewydo.idlemergecubes.game.utils.actor.setPosition
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import com.lewydo.idlemergecubes.game.utils.stateMachine.StateMachine
import kotlinx.coroutines.launch

class APanelMergeBonus(override val screen: AdvancedScreen): AConstraintLayout(screen) {

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
    private val aCoinImg  = Image(gdxGame.assetsAll.coin)
    private val aCoinLbl  = Label("0", FontFactory.create(screen, parameterCoins, screen.fontGenerator_Nunito_SemiBold))

    //private val aPanelMergeBonusImg = Image(gdxGame.assetsAll.PANEL_IDLE)

    private val aPanelProgressMergeBonus = APanelProgressMergeBonus(screen)
    private val aPanelCollectMergeBonus  = APanelCollectMergeBonus(screen)

    private val listConfettiEffect = List(8) { AParticleEffectActor(ParticleEffect(gdxGame.particleEffectAll.IDLE_CONFETTI)) }
    private val aWaveEffect        = AParticleEffectActor(gdxGame.particleEffectAll.IDLE_WAVE)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------

    private val stateMachine = StateMachine()
    private val stateFilling = FillingState(stateMachine, aPanelProgressMergeBonus)
    private val stateReady   = ReadyState(stateMachine, aPanelCollectMergeBonus) {
        listConfettiEffect.forEach { it.start() }
        aWaveEffect.start()

        gdxGame.soundUtil.apply { play(SHOW_COLLECT) }
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        //addPanelMergeBonusImg()
        addTitleLbl()
        addCoinsLbl()
        addCoinImg()

        addPanelProgressMergeBonus()
        addPanelCollectMergeBonus()

        addEffectConfetti()
        addEffectWave()

        collectMergeBonusProgress()

        stateMachine.setState(stateFilling)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

//    private fun addPanelMergeBonusImg() {
//        addAndFillActor(aPanelMergeBonusImg)
//    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(529f, 98f)
        add(aTitleLbl) { startToStart(margin = 48f); topToTop(margin = 73f) }
    }

    private fun addCoinImg() {
        aCoinImg.setSize(70f, 70f)
        add(aCoinImg) { endToEnd(margin = 58f); topToTop(margin = 78f) }
    }

    private fun addCoinsLbl() {
        aCoinLbl.setSize(39f, 87f)
        add(aCoinLbl) { endToStart(aCoinImg, 8f); centerY(aCoinImg) }
        aCoinLbl.setAlignment(Align.right)
    }

    private fun addPanelProgressMergeBonus() {
        add(aPanelProgressMergeBonus) { fillParent() }

        aPanelProgressMergeBonus.onFinished = {
            stateMachine.setState(stateReady)
        }
    }

    private fun addPanelCollectMergeBonus() {
        aPanelCollectMergeBonus.animHideAndDisable()
        add(aPanelCollectMergeBonus) { fillParent() }

        aPanelCollectMergeBonus.apply {
            onCollect = {
                gdxGame.modelMergeBonus.collect()
                stateMachine.setState(stateFilling)
            }
            onCollectX2 = {
                stateMachine.setState(stateFilling)

                val adManager = gdxGame.activity.adManager
                if (adManager.rewarded.isReady) {
                    adManager.rewarded.show(
                        onEarned = {
                            gdxGame.modelMergeBonus.collectX2()
                            gdxGame.analytics.adWatched("merge_bonus_x2")
                        },
                        onDismissed = {},
                        onFailed = {
                            runGDX { gdxGame.modelMergeBonus.collect() }
                        }
                    )
                } else {
                    gdxGame.modelMergeBonus.collect()
                }
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
        //aWaveEffect.setSize(1f, 1f)
        aWaveEffect.fitToSize(targetWidth = width, baseWidth = BASE_WIDTH_EFFECT)
        addActorAligned(aWaveEffect, AlignH.CENTER, AlignV.CENTER)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectMergeBonusProgress() {
        coroutine?.launch {
            gdxGame.modelMergeBonus.rewardFlow.collect { reward ->
                runGDX {
                    aCoinLbl.setText(NumberFormatter.format(reward))
                    aPanelCollectMergeBonus.setReward(reward)
                }
            }
        }

        coroutine?.launch {
            gdxGame.modelMergeBonus.progressFlow.collect { progress ->
                runGDX {
                    val count = gdxGame.modelMergeBonus.countFlow.value
                    val goal  = gdxGame.modelMergeBonus.goalFlow.value
                    aPanelProgressMergeBonus.updateProgress(count, goal)

                    if (progress >= 1f && stateMachine.getCurrentState() is FillingState) stateMachine.setState(stateReady)
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
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

}