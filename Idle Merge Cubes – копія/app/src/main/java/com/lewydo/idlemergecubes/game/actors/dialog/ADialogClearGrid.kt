package com.lewydo.idlemergecubes.game.actors.dialog

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ADialogClearGrid(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDialogImg = Image(gdxGame.assetsAll.DIALOG_CLEAR_GRID)
    private val aYesBtn    = AButton(screen, AButton.Type.YES)
    private val aNoBtn     = AButton(screen, AButton.Type.NO)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------

    var blockYes = {}
    var blockNo  = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addDialogImg()
        addYesBtn()
        addNoBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDialogImg() {
        addAndFillActor(aDialogImg)
    }

    private fun addYesBtn() {
        addActor(aYesBtn)
        aYesBtn.setBounds(120f, 56f, 573f, 276f)
        aYesBtn.setOnClickListener { blockYes() }
    }

    private fun addNoBtn() {
        addActor(aNoBtn)
        aNoBtn.setBounds(740f, 56f, 573f, 276f)
        aNoBtn.setOnClickListener { blockNo() }
    }

}