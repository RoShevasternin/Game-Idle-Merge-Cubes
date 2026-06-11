package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.APanelMergeBonus
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class APanelMergeBonusWithGoals(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg              = Image(gdxGame.assetsAll.panel_merge_bonus)
    private val aPanelMergeBonus    = APanelMergeBonus(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    //var onClickSettingsBtn = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addPanelMergeBonus()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addPanelMergeBonus() {
        aPanelMergeBonus.setSize(width, 428f)
        add(aPanelMergeBonus) { centerX(); topToTop() }
    }

}