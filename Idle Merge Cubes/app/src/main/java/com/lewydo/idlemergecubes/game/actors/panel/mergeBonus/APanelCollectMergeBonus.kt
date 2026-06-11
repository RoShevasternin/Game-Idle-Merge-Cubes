package com.lewydo.idlemergecubes.game.actors.panel.mergeBonus

import com.lewydo.idlemergecubes.game.actors.button.ACollectButton
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelCollectMergeBonus(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCollectBtn   = ACollectButton(screen, ACollectButton.Type.COLLECT)
    private val aCollectX2Btn = ACollectButton(screen, ACollectButton.Type.COLLECT_X2)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------

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
        aCollectBtn.blockClick = { onCollect.invoke() }
    }

    private fun addCollectX2() {
        addActor(aCollectX2Btn)
        aCollectX2Btn.setBounds(968f, 56f, 889f, 202f)
        aCollectX2Btn.blockClick = { onCollectX2.invoke() }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setReward(amount: Int) {
        aCollectBtn.setReward(amount)
        aCollectX2Btn.setReward(amount * 2)
    }

}