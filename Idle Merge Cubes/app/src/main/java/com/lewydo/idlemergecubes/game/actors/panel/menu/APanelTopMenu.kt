package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class APanelTopMenu(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textTitle = "MENU"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoBold, 160f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelMenuImg = Image(gdxGame.assetsAll.PANEL_MENU)
    private val aTitleLbl     = AMsdfLabel(textTitle, styleDef)

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