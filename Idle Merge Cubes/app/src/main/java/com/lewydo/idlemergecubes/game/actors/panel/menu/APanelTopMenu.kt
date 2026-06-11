package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class APanelTopMenu(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textTitle = "MENU"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter = FontParameter().setCharacters(textTitle).setSize(160)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelMenuImg = Image(gdxGame.assetsAll.PANEL_MENU)
    private val aTitleLbl     = Label(textTitle, FontFactory.create(screen, parameter, screen.fontGenerator_Nunito_Bold))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addPanelMenuImg()
        addTitleLbl()

    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanelMenuImg() {
        addAndFillActor(aPanelMenuImg)
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(473f, 218f)
        addActorWithConstraints(aTitleLbl) {
            startToStartOf = this@APanelTopMenu
            endToEndOf     = this@APanelTopMenu
            topToTopOf     = this@APanelTopMenu

            marginTop = 35f
        }

        aTitleLbl.setAlignment(Align.center)
    }

}