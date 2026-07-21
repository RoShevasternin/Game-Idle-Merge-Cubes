package com.lewydo.idlemergecubes.game.actors.dialog

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
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.vfx.AMask
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ADialogLevelUp(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textLevel      = "LEVEL"
    private val textBonusCoins = "Bonus coins"

    private val BASE_WIDTH_EFFECT = 1908f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleLevel = MsdfStyle(msdf, msdf.fontNunitoExtraBold, 200f)
    private val styleValue = MsdfStyle(msdf, msdf.fontNunitoExtraBold, 165f)
        .stroke(2f, GameColor.brown_683E03)
        .dropShadow(5f, 0f, 4f, GameColor.brown_683E03)
    private val styleCoins = styleValue.copy(font = msdf.fontNunitoRegular, size = 80f, color = GameColor.dark_brown_360000)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDialogImg    = Image(gdxGame.assetsAll.DIALOG_LEVEL_UP)
    private val aCollectBtn   = ACollectButton(screen, ACollectButton.Type.COLLECT)
    private val aCollectX2Btn = ACollectButton(screen, ACollectButton.Type.COLLECT_X2)

    private val aCircleStrokeFill = ACircleStrokeFill(screen)
    private val aLevelLbl         = AMsdfLabel(textLevel, styleLevel)

    private val aBonusPanelGroup  = ATmpGroup(screen)
    private val aBonusPanelImg    = Image(gdxGame.assetsAll.PANEL_LEVEL_UP_BONUS)
    private val aCoinImg          = Image(gdxGame.assetsAll.COIN_BIG)
    private val aBonusValueLbl    = AMsdfLabel("0", styleValue)
    private val aBonusCoinsLbl    = AMsdfLabel(textBonusCoins, styleCoins)

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
        aMask.autoCache = false
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

    fun stopEffect() {
        aEffectConfetti.pause()
    }



}