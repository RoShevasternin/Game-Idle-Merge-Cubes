package com.lewydo.idlemergecubes.game.actors.panel.goals.state

import com.lewydo.idlemergecubes.game.actors.panel.goals.APanelGoals
import com.lewydo.idlemergecubes.game.utils.stateMachine.IStateMachine
import com.lewydo.idlemergecubes.game.utils.stateMachine.State

// ═════════════════════════════════════════════════════════════════════════════
//  FailedState — задача провалена: overlay "Failed!"
// ═════════════════════════════════════════════════════════════════════════════

class FailedState(
    override val stateMachine: IStateMachine,
    private val panel        : APanelGoals,
) : State() {

    override fun onEnter() {
        panel.showFailedOverlay()
    }
}