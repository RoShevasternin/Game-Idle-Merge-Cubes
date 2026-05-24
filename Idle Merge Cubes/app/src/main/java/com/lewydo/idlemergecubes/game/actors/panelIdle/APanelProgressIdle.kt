package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter

class APanelProgressIdle(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "/ merges").setSize(56)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aProgressIdle = AProgressIdle(screen)
    private val aBagCoins     = ABagCoins(screen)
    private val aCounterLbl   = Label("0 / 10 merges", FontFactory.create(screen, parameter, screen.fontGenerator_Nunito_Medium))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinished: Block = {}
        set(value) { field = value; aProgressIdle.onFinished = value }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addProgressIdle()
        addBagCoins()
        addCounterLbl()
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

    private fun addCounterLbl() {
        addActor(aCounterLbl)
        aCounterLbl.setBounds(1140f, 256f, 383f, 76f)
        aCounterLbl.setAlignment(Align.right)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun updateProgress(count: Int, goal: Int) {
        val pct = (count.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        aCounterLbl.setText("$count / $goal merges")
        aProgressIdle.setProgress(pct)
        aBagCoins.setProgress(pct)
    }

}