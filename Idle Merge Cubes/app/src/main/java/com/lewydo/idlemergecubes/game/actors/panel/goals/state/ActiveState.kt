package com.lewydo.idlemergecubes.game.actors.panel.goals.state

import com.lewydo.idlemergecubes.game.actors.panel.goals.APanelGoals
import com.lewydo.idlemergecubes.game.utils.stateMachine.IStateMachine
import com.lewydo.idlemergecubes.game.utils.stateMachine.State

// ═════════════════════════════════════════════════════════════════════════════
//  ActiveState — задача активна: ховаємо result overlay
//
//  Вибір та видимість панелі задаються в APanelGoals.bindGoal() (це дані),
//  стан керує лише overlay — тому порядок flow-подій не має значення.
// ═════════════════════════════════════════════════════════════════════════════

class ActiveState(
    override val stateMachine: IStateMachine,
    private val panel        : APanelGoals,
) : State() {

    override fun onEnter() {
        panel.hideResultOverlay()
    }
}