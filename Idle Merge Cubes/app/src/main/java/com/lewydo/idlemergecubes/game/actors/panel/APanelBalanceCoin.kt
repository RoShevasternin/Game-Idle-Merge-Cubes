package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfLabel
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class APanelBalanceCoin(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelCoinImg = Image(gdxGame.assetsAll.panel_coin)
    private val aCoinLbl      = MsdfLabel(msdf, msdf.fontNunitoBold, "", 83f, GameColor.yellow_FFF858).addEffect(msdf.dropShadow(7f, 7f, 4f, GameColor.purple_350080))
    private val aCoinImg      = Image(gdxGame.assetsAll.coin)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addPanelCoinImg()
        addCoinLbl()
        addCoinImg()

        collectCoins()
        collectShakeEvent()

        registerStagePosition()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanelCoinImg() {
        addActor(aPanelCoinImg)
        aPanelCoinImg.setBounds(59f, 0f, 482f, 175f)
    }

    private fun addCoinLbl() {
        aCoinLbl.debug()
        addActor(aCoinLbl)
        aCoinLbl.setPosition(236f, 37f)
        aCoinLbl.setSize(1f, 114f)
    }

    private fun addCoinImg() {
        addActor(aCoinImg)
        aCoinImg.setBounds(0f, 7f, 175f, 175f)
        aCoinImg.setOrigin(Align.center)
        startSimpleSway()
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectCoins() {
        coroutine?.launch {
            gdxGame.modelPlayer.coinsFlow.collect { coins ->
                runGDX { updateCoinsUI(coins) }
            }
        }
    }

    private fun collectShakeEvent() {
        coroutine?.launch {
            GlobalEvents.events
                .filter { it == GlobalEvents.EventType.END_FLY_COIN }
                .collect { runGDX { animShake() } }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun updateCoinsUI(coins: Long) {
        aCoinLbl.setText(NumberFormatter.format(coins))
        aCoinLbl.pack()

        if (aCoinLbl.width >= 270f) {
            val newWidth = 150f + aCoinLbl.width + 100f
            aPanelCoinImg.setSize(newWidth, aPanelCoinImg.height)
        }
    }

    private fun registerStagePosition() {
        GlobalStagePositions.register(
            key      = GlobalStagePositions.Key.COIN,
            actor    = aCoinImg,
            offsetX  = aCoinImg.width  / 2f,
            offsetY  = aCoinImg.height / 2f,
        )
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun startSimpleSway() {
        aCoinImg.clearActions()
        aCoinImg.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.rotateTo( 10f, 1.8f, Interpolation.sine),
                    Actions.rotateTo(-10f, 1.8f, Interpolation.sine),
                )
            )
        )
    }

    fun animShake() {
        aCoinImg.clearActions()
        aCoinImg.addAction(
            Actions.sequence(
                Actions.scaleTo(1.08f, 1.08f, 0.1f, Interpolation.sineOut),
                Actions.scaleTo(1.0f,  1.0f,  0.2f, Interpolation.sineOut),
                Actions.run { startSimpleSway() }
            )
        )
    }
}