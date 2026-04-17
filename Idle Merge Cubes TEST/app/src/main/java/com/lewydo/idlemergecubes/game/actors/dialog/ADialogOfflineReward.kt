package com.lewydo.idlemergecubes.game.actors.dialog

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ACircleStrokeFill
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectActor
import com.lewydo.idlemergecubes.game.actors.button.ACollectButton
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.vfx.AMaskOLD
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ADialogOfflineReward(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textTitle    = "YOU’VE BEEN AWAY FOR"
    private val textSubTitle = "WHILE YOU WERE RESTING, WE COLLECTED FOR YOU:"

    private val BASE_WIDTH_EFFECT = 1908f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter    = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val fontTitle    = screen.fontGenerator_Nunito_ExtraBold.generateFont(parameter.setSize(140).setShadow(7, 7, GameColor.brown_683E03))
    private val fontSubTitle = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(80).setShadow(0, 0, Color.BLACK))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDialogImg    = Image(gdxGame.assetsAll.DIALOG_OFFLINE)
    private val aCollectBtn   = ACollectButton(screen, ACollectButton.Type.COLLECT)
    private val aCollectX2Btn = ACollectButton(screen, ACollectButton.Type.COLLECT_X2)

    private val aCircleStrokeFill = ACircleStrokeFill(screen)
    private val aTitleLbl         = Label(textTitle, Label.LabelStyle(fontTitle, Color.WHITE))
    private val aSubTitleLbl      = Label(textSubTitle, Label.LabelStyle(fontSubTitle, Color.WHITE))

    private val aMask           = AMaskOLD(screen, gdxGame.assetsAll.MASK_DIALOG_OFFLINE)
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
        addTitleLbl()
        addSubTitleLbl()
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

        val aCoinImg = Image(gdxGame.assetsAll.COIN_BIG)
        aCoinImg.setSize(500f, 500f)
        aCircleStrokeFill.addActorAligned(aCoinImg, AlignH.CENTER, AlignV.CENTER)

        aCoinImg.setOrigin(Align.center)
        aCoinImg.addAction(Actions.forever(Actions.sequence(
            Actions.rotateTo(15f, 0.6f, Interpolation.sineOut), // вправо
            Actions.rotateTo(-15f, 1.2f, Interpolation.sine),   // вліво
            Actions.rotateTo(0f, 0.6f, Interpolation.sineIn),   // повертається
        )))
    }

    private fun addTitleLbl() {
        addActor(aTitleLbl)
        aTitleLbl.setBounds(50f, 1126f, 1807f, 382f)
        aTitleLbl.setAlignment(Align.center)
        aTitleLbl.wrap = true
    }

    private fun addSubTitleLbl() {
        addActor(aSubTitleLbl)
        aSubTitleLbl.setBounds(146f, 878f, 1601f, 218f)
        aSubTitleLbl.setAlignment(Align.center)
        aSubTitleLbl.wrap = true
    }

    private fun addMask() {
        addAndFillActor(aMask)
        aMask.addEffectConfetti()
    }

    private fun AMaskOLD.addEffectConfetti() {
        aEffectConfetti.fitToSize(
            targetWidth = width,
            baseWidth   = BASE_WIDTH_EFFECT,
        )
        addActorAligned(aEffectConfetti, AlignH.LEFT, AlignV.TOP)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setReward(amount: Long) {
        aCollectBtn.setReward(amount.toInt())
        aCollectX2Btn.setReward((amount * 2).toInt())
    }

    fun setDuration(duration: String) {
        aTitleLbl.setText("YOU'VE BEEN AWAY FOR $duration!")
    }

    fun startEffect() {
        aEffectConfetti.start()
    }

    fun stopEffect() {
        aEffectConfetti.pause()
    }

}