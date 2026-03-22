package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelCollectIdle(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCollectBtn   = AButton(screen, AButton.Type.COLLECT)
    private val aCollectX2Btn = AButton(screen, AButton.Type.COLLECT_X2)

    var onCollect  : Block = {}
    var onCollectX2: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCollect()
        addCollectX2()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCollect() {
        addActor(aCollectBtn)
        aCollectBtn.setBounds(48f, 56f, 889f, 202f)
        aCollectBtn.setOnClickListener {
            onCollect()
        }
    }

    private fun addCollectX2() {
        addActor(aCollectX2Btn)
        aCollectX2Btn.setBounds(968f, 56f, 889f, 202f)
        aCollectX2Btn.setOnClickListener {
            onCollectX2()
        }
    }


}