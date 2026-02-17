package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AdvancedGroup() {

    private val aPanelTopImg      = Image(gdxGame.assetsAll.PANEL_TOP)
    private val aSettingsBtn      = AButton(screen, AButton.Type.Settings)
    private val aPanelBalanceCoin = APanelBalanceCoin(screen)
    private val aPanelLvL         = APanelLvL(screen)

    override fun addActorsOnGroup() {
        addPanelTopImg()
        addSettingsBtn()
        addPanelBalanceCoinGroup()
        addPanelLvL()
    }

    // Actors ------------------------------------------------------------------------

    private fun addPanelTopImg() {
        addAndFillActor(aPanelTopImg)
    }

    private fun addSettingsBtn() {
        addActor(aSettingsBtn)
        aSettingsBtn.setBounds(1796f, 87f, 236f, 236f)
        aSettingsBtn.setOnClickListener {

        }
    }

    private fun addPanelBalanceCoinGroup() {
        addActor(aPanelBalanceCoin)
        aPanelBalanceCoin.setBounds(430f, 113f, 568f, 175f)
    }

    private fun addPanelLvL() {
        addActor(aPanelLvL)
        aPanelLvL.setBounds(107f, 69f, 270f, 270f)
    }

}