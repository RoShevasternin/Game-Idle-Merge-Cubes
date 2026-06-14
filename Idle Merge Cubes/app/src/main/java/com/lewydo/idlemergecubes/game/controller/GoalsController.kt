package com.lewydo.idlemergecubes.game.controller

import com.lewydo.idlemergecubes.game.actors.panel.goals.APanelGoals
import com.lewydo.idlemergecubes.game.actors.panel.goals.state.ActiveState
import com.lewydo.idlemergecubes.game.actors.panel.goals.state.CompletedState
import com.lewydo.idlemergecubes.game.actors.panel.goals.state.FailedState
import com.lewydo.idlemergecubes.game.model.GoalsModel
import com.lewydo.idlemergecubes.game.utils.runGDX
import com.lewydo.idlemergecubes.game.utils.stateMachine.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  GoalsController — міст між GoalsModel (логіка/flows) та APanelGoals (UI)
//
//  Панель НЕ знає про модель. Контролер:
//    • підписується на flows моделі
//    • оновлює UI панелі (goal/progress/timer/counter)
//    • перемикає state-машину панелі (Active/Completed/Failed)
//
//  scope передаємо ззовні (coroutine панелі), щоб lifecycle збігався з UI.
// ═════════════════════════════════════════════════════════════════════════════
class GoalsController(
    private val model: GoalsModel,
    private val panel: APanelGoals,
    private val scope: CoroutineScope,
) {

    // ── State machine ─────────────────────────────────────────────────────────

    private val stateMachine   = StateMachine()
    private val activeState    = ActiveState(stateMachine, panel)
    private val completedState = CompletedState(stateMachine, panel)
    private val failedState    = FailedState(stateMachine, panel)

    // ── Bind ────────────────────────────────────────────────────────────────

    fun bind() {
        collectGoal()
        collectProgress()
        collectTimer()
        collectState()
        collectCounter()
    }

    // ── Collect ───────────────────────────────────────────────────────────────

    private fun collectGoal() {
        scope.launch {
            model.currentGoalFlow.collect { goal ->
                goal ?: return@collect
                runGDX { panel.bindGoal(goal) }
            }
        }
    }

    private fun collectProgress() {
        scope.launch {
            model.progressFlow.collect { progress ->
                progress ?: return@collect
                runGDX { panel.bindProgress(progress) }
            }
        }
    }

    private fun collectTimer() {
        scope.launch {
            model.timerFlow.collect { seconds ->
                runGDX { panel.bindTimer(seconds) }
            }
        }
    }

    private fun collectState() {
        scope.launch {
            model.stateFlow.collect { state ->
                runGDX { applyState(state) }
            }
        }
    }

    private fun collectCounter() {
        scope.launch {
            model.goalCounterFlow.collect { counter ->
                runGDX { panel.bindCounter(counter) }
            }
        }
    }

    // ── State routing ─────────────────────────────────────────────────────────

    private fun applyState(state: GoalsModel.State) {
        when (state) {
            GoalsModel.State.ACTIVE    -> stateMachine.setState(activeState)
            GoalsModel.State.COMPLETED -> {
                completedState.reward = model.currentGoal?.reward ?: 0L
                stateMachine.setState(completedState)
            }
            GoalsModel.State.FAILED    -> stateMachine.setState(failedState)
        }
    }
}