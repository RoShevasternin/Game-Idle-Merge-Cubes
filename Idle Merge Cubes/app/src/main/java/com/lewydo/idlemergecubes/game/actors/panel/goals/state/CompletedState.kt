package com.lewydo.idlemergecubes.game.actors.panel.goals.state

import com.lewydo.idlemergecubes.game.actors.panel.goals.APanelGoals
import com.lewydo.idlemergecubes.game.utils.stateMachine.IStateMachine
import com.lewydo.idlemergecubes.game.utils.stateMachine.State

// ═════════════════════════════════════════════════════════════════════════════
//  CompletedState — задача виконана: overlay "+reward"
// ═════════════════════════════════════════════════════════════════════════════

class CompletedState(
    override val stateMachine: IStateMachine,
    private val panel        : APanelGoals,
) : State() {

    var reward: Long = 0L

    override fun onEnter() {
        panel.showCompletedOverlay(reward)
    }
}