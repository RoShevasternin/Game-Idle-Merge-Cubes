package com.lewydo.idlemergecubes.game.actors.panel.mergeBonus

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter

class APanelProgressMergeBonus(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "/ merges").setSize(56)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aProgressMergeBonus = AProgressMergeBonus(screen)
    private val aBagCoins           = ABagCoins(screen)
    private val aCounterLbl         = Label("0 / 10 merges", FontFactory.create(screen, parameter, screen.fontGenerator_Nunito_Medium))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinished: Block = {}
        set(value) { field = value; aProgressMergeBonus.onFinished = value }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addProgressMergeBonus()
        addBagCoins()
        addCounterLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addProgressMergeBonus() {
        aProgressMergeBonus.setSize(1576f, 74f)
        add(aProgressMergeBonus) { startToStart(margin = 48f); bottomToBottom(margin = 94f) }
    }

    private fun addBagCoins() {
        aBagCoins.setSize(180f, 117f)
        add(aBagCoins) { endToEnd(margin = 58f); bottomToBottom(margin = 79f) }
    }

    private fun addCounterLbl() {
        add(aCounterLbl) { endToEnd(aProgressMergeBonus, margin = 5f); bottomToTop(aProgressMergeBonus, 16f) }
        aCounterLbl.setAlignment(Align.right)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun updateProgress(count: Int, goal: Int) {
        val pct = (count.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        aCounterLbl.setText("$count / $goal merges")
        aProgressMergeBonus.setProgress(pct)
        aBagCoins.setProgress(pct)
    }

}