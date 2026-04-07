package com.lewydo.idlemergecubes.game.actors.dialog

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ACircleStrokeFill
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectActor
import com.lewydo.idlemergecubes.game.actors.button.ACollectButton
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.shader.AMask
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.setBounds
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ADialogLevelUp(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textLevel      = "LEVEL"
    private val textBonusCoins = "Bonus coins"

    private val BASE_WIDTH_EFFECT = 1908f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter      = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + textLevel + textBonusCoins + ",!")
    private val fontLevel      = screen.fontGenerator_Nunito_ExtraBold.generateFont(parameter.setSize(200))
    private val fontBonusCoins = screen.fontGenerator_Nunito_Regular.generateFont(parameter.setSize(80))
    private val fontBonusValue = screen.fontGenerator_Nunito_ExtraBold.generateFont(parameter.setSize(165).setBorder(2f, GameColor.brown_683E03).setShadow(5, 0, GameColor.brown_683E03))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDialogImg    = Image(gdxGame.assetsAll.DIALOG_LEVEL_UP)
    private val aCollectBtn   = ACollectButton(screen, ACollectButton.Type.COLLECT)
    private val aCollectX2Btn = ACollectButton(screen, ACollectButton.Type.COLLECT_X2)

    private val aCircleStrokeFill = ACircleStrokeFill(screen)
    private val aLevelLbl         = Label(textLevel, Label.LabelStyle(fontLevel, Color.WHITE))

    private val aBonusPanelGroup  = ATmpGroup(screen)
    private val aBonusPanelImg    = Image(gdxGame.assetsAll.PANEL_LEVEL_UP_BONUS)
    private val aCoinImg          = Image(gdxGame.assetsAll.COIN_BIG)
    private val aBonusValueLbl    = Label("0", Label.LabelStyle(fontBonusValue, Color.WHITE))
    private val aBonusCoinsLbl    = Label(textBonusCoins, Label.LabelStyle(fontBonusCoins, GameColor.dark_brown_360000))

    private val aMask           = AMask(screen, gdxGame.assetsAll.MASK_DIALOG_LEVEL_UP)
    private val aEffectConfetti = AParticleEffectActor(ParticleEffect(gdxGame.particleEffectAll.CONFETTI))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------

    var onCollect  : Block = {}
    var onCollectX2: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addDialogImg()
        addMask()

        addCollect()
        addCollectX2()

        addCircle()
        addLevelLbl()
        addBonusPanelGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDialogImg() {
        addAndFillActor(aDialogImg)
    }

    private fun addCollect() {
        addActor(aCollectBtn)
        aCollectBtn.setBounds(100f, 429f, 1707f, 276f)
        aCollectBtn.blockClick = { onCollect.invoke() }
    }

    private fun addCollectX2() {
        addActor(aCollectX2Btn)
        aCollectX2Btn.setBounds(100f, 120f, 1707f, 276f)
        aCollectX2Btn.blockClick = { onCollectX2.invoke() }
    }

    private fun addCircle() {
        addActor(aCircleStrokeFill)
        aCircleStrokeFill.setBounds(612f, 1537f, 670f, 670f)

        val aConfettiImg = Image(gdxGame.assetsAll.CONFETTI)
        aConfettiImg.setSize(463f, 463f)
        aCircleStrokeFill.addActorAligned(aConfettiImg, AlignH.CENTER, AlignV.CENTER)

        aConfettiImg.setOrigin(Align.center)
        aConfettiImg.addAction(Actions.forever(Actions.sequence(
            Actions.rotateTo(15f, 0.6f, Interpolation.sineOut), // вправо
            Actions.rotateTo(-15f, 1.2f, Interpolation.sine),   // вліво
            Actions.rotateTo(0f, 0.6f, Interpolation.sineIn),   // повертається
        )))
    }

    private fun addLevelLbl() {
        addActor(aLevelLbl)
        aLevelLbl.setBounds(528f, 1215f, 845f, 273f)
        aLevelLbl.setAlignment(Align.center)
    }

    private fun addBonusPanelGroup() {
        addActor(aBonusPanelGroup)
        aBonusPanelGroup.setBounds(100f, 786f, 1707f, 380f)

        aBonusPanelGroup.addAndFillActor(aBonusPanelImg)
        aBonusPanelGroup.addActors(aCoinImg, aBonusValueLbl, aBonusCoinsLbl)

        aCoinImg.setBounds(653f, 150f, 170f, 170f)
        aBonusValueLbl.setBounds(848f, 123f, 205f, 225f)
        aBonusCoinsLbl.setBounds(637f, 32f, 432f, 109f)
    }

    private fun addMask() {
        addAndFillActor(aMask)
        aMask.addEffectConfetti()
    }

    private fun AMask.addEffectConfetti() {
        aEffectConfetti.fitToSize(
            targetWidth = width,
            baseWidth   = BASE_WIDTH_EFFECT,
        )
        addActorAligned(aEffectConfetti, AlignH.LEFT, AlignV.TOP)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setLevel(level: Int) {
        aLevelLbl.setText("LEVEL $level!")
    }

    fun setReward(amount: Long) {
        aBonusValueLbl.setText(NumberFormatter.format(amount))
        aCollectBtn.setReward(amount.toInt())
        aCollectX2Btn.setReward((amount * 2).toInt())
    }

    fun startEffect() {
        aEffectConfetti.start()
    }



}