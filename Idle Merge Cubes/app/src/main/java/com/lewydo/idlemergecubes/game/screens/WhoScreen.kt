package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.who_made.ABrand
import com.lewydo.idlemergecubes.game.actors.who_made.ADescription
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class WhoScreen: AdvancedScreen() {

    private val textTitle = "Who Made This Game"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(160)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aBackBtn      = AButtonTexture(this, AButtonStyles.Texture.BACK)
    private val aTitleLbl     = Label(textTitle, FontFactory.create(this, parameter, fontGenerator_Nunito_SemiBold))
    private val aBrand        = ABrand(this)
    private val aSeparatorImg = Image(gdxGame.assetsAll.separator)
    private val aDescription  = ADescription(this)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()
    }

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addBackBtn()
        addTitleLbl()
        addBrand()
        addSeparatorImg()
        addDescription()

        animShowScreen()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun Group.addBackBtn() {
        aBackBtn.setSize(236f, 236f)
        addActorWithConstraints(aBackBtn) {
            startToStartOf = this@addBackBtn
            topToTopOf     = this@addBackBtn
            marginStart    = 128f
            marginTop      = 78f
        }

        aBackBtn.setOnClickListener { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun Group.addTitleLbl() {
        aTitleLbl.setSize(1851f, 218f)
        aTitleLbl.setAlignment(Align.center)
        addActorWithConstraints(aTitleLbl) {
            startToStartOf = this@addTitleLbl
            endToEndOf     = this@addTitleLbl
            topToBottomOf  = aBackBtn
            marginTop      = 101f
        }
    }

    private fun Group.addBrand() {
        aBrand.setSize(864f, 265f)
        addActorWithConstraints(aBrand) {
            startToStartOf = this@addBrand
            endToEndOf     = this@addBrand
            topToBottomOf  = aTitleLbl
            marginTop      = 32f
        }
    }

    private fun Group.addSeparatorImg() {
        aSeparatorImg.setSize(1908f, 3f)
        addActorWithConstraints(aSeparatorImg) {
            startToStartOf = this@addSeparatorImg
            endToEndOf     = this@addSeparatorImg
            topToBottomOf  = aBrand
            marginTop      = 28f
        }
    }

    private fun Group.addDescription() {
        val topMarge    = 52f
        val bottomMarge = 126f

        val nHeight = aSeparatorImg.y - (topMarge + bottomMarge)
        aDescription.setSize(1908f, nHeight)

        addActorWithConstraints(aDescription) {
            startToStartOf = this@addDescription
            endToEndOf     = this@addDescription
            topToBottomOf  = aSeparatorImg
            marginTop      = topMarge
        }
    }

}