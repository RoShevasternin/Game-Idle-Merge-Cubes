package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.actors.layout.linear.AVerticalGroup
import com.lewydo.idlemergecubes.game.actors.panelMenu.settings.ASettingsSection
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelContentMenu(override val screen: AdvancedScreen): AVerticalGroup(
    screen,
    gap = 48f,
    wrap = true
) {

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
        addLeaderboardBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLeaderboardBtn() {
        aLeaderboardBtn.setSize(1916f, 276f)
        addActor(aLeaderboardBtn)
    }

}