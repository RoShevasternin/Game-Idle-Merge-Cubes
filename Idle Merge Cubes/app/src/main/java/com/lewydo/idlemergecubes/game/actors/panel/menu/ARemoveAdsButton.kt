package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.REMOVE_ADS_PRICE
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

open class ARemoveAdsButton(override val screen: AdvancedScreen) : AButtonTexture(screen, AButtonStyles.Texture.MENU_ITEM) {

    private val textRemoveAds = "Remove Ads"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoBold, 80f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTitleLbl = AMsdfLabel(textRemoveAds, styleDef)
    private val aPriceLbl = AMsdfLabel("$$REMOVE_ADS_PRICE", styleDef, color = GameColor.green_98FF68)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        addTitleLbl()
        addPriceLbl()

        // todo: remove
        addAndFillActor(Image(gdxGame.assetsAll.ComingSoon))
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addTitleLbl() {
        addActor(aTitleLbl)
        aTitleLbl.setBounds(80f, 83f, 466f, 109f)
        aTitleLbl.disable()
    }

    private fun addPriceLbl() {
        addActor(aPriceLbl)
        aPriceLbl.setBounds(1624f, 83f, 212f, 109f)
        aPriceLbl.disable()
        aPriceLbl.setAlignment(Align.right)
    }

}