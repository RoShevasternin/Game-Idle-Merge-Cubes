package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelProgressIdle(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aProgressIdle = AProgressIdle(screen)
    private val aBagCoins     = ABagCoins(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinished: Block = {}
        set(value) {
            field = value
            aProgressIdle.onFinished = value
        }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addProgressIdle()
        addBagCoins()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addProgressIdle() {
        addActor(aProgressIdle)
        aProgressIdle.setBounds(51f, 122f, 1573f, 78f)
    }

    private fun addBagCoins() {
        addActor(aBagCoins)
        aBagCoins.setBounds(1667f, 108f, 180f, 117f)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun startIdleCycle(duration: Float) {
        aProgressIdle.startFill(duration)
        aBagCoins.startFill(duration)
    }

}