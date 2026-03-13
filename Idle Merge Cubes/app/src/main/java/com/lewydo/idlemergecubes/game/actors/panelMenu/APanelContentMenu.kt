package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.actors.panelMenu.settings.ASettingsSection
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelContentMenu(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aLeaderboardBtn  = ALeaderboardButton(screen)
    private val aRemoveAdsBtn    = ARemoveAdsButton(screen)
    private val aSettingsSection = ASettingsSection(screen)
    private val aResetGameBtn    = AButton(screen, AButton.Type.MENU_RESET_GAME)
    private val aCloseBtn        = AButton(screen, AButton.Type.MENU_CLOSE)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        //addPanelMenuImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

   // private fun addPanelMenuImg() {
   //     addAndFillActor(aPanelMenuImg)
   // }

}