package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.REMOVE_ADS_PRICE
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

open class ARemoveAdsButton(override val screen: AdvancedScreen) : AButton(screen, Type.MENU_ITEM) {

    private val textRemoveAds = "Remove Ads"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters("$textRemoveAds $REMOVE_ADS_PRICE $")
    private val font = screen.fontGenerator_Nunito_Bold.generateFont(parameter.setSize(80))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTitleLbl = Label(textRemoveAds, Label.LabelStyle(font, Color.WHITE))
    private val aPriceLbl = Label("$$REMOVE_ADS_PRICE", Label.LabelStyle(font, GameColor.green_98FF68))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        addTitleLbl()
        addPriceLbl()
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