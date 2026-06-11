package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.data.GoalRequirement
import com.lewydo.idlemergecubes.game.data.GoalState
import com.lewydo.idlemergecubes.game.state.GameState
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalContext
import com.lewydo.idlemergecubes.game.systems.goals.GoalGenerator
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GoalsModel(
    private val state        : GameState,
    private val playerModel  : PlayerModel,
    private val buyLevelModel: BuyLevelModel,
    private val scope        : CoroutineScope,
) {

    // ── State enum ────────────────────────────────────────────────────────────

    enum class State { ACTIVE, COMPLETED, FAILED }

    // ── Public flows ──────────────────────────────────────────────────────────

    private val _currentGoalFlow = MutableStateFlow<Goal?>(null)
    private val _progressFlow    = MutableStateFlow<GoalProgress?>(null)
    private val _stateFlow       = MutableStateFlow(State.ACTIVE)
    private val _timerFlow       = MutableStateFlow(0)
    private val _goalCounterFlow = MutableStateFlow(0)

    val currentGoalFlow : StateFlow<Goal?>         = _currentGoalFlow.asStateFlow()
    val progressFlow    : StateFlow<GoalProgress?> = _progressFlow.asStateFlow()
    val stateFlow       : StateFlow<State>         = _stateFlow.asStateFlow()
    val timerFlow       : StateFlow<Int>           = _timerFlow.asStateFlow()
    val goalCounterFlow : StateFlow<Int>           = _goalCounterFlow.asStateFlow()

    val currentGoal: Goal? get() = _currentGoalFlow.value

    // ── Auto-init ─────────────────────────────────────────────────────────────

    init {
        scope.launch {
            state.gridFlow.filter { it.size == 16 }.first()
            initGoal()
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun checkCompletion() {
        if (_stateFlow.value != State.ACTIVE) return
        refreshProgress()

        val done = when (val p = _progressFlow.value) {
            is GoalProgress.Simple   -> p.isDone
            is GoalProgress.Combined -> p.isDone
            null                     -> false
        }
        if (done) completeGoal()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        state.goalState = state.goalState.copy(timerRemaining = _timerFlow.value)
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    private fun initGoal() {
        _goalCounterFlow.value = state.goalState.counter

        val restored = restoreFromState()
        val goal     = restored ?: generateAndSave()

        _currentGoalFlow.value = goal
        _stateFlow.value       = State.ACTIVE
        refreshProgress()

        if (goal is Goal.Timed) {
            val remaining = state.goalState.timerRemaining
            when {
                remaining > 0    -> startTimer(remaining)
                restored != null -> failGoal()
                else             -> startTimer(goal.timeLimitSec)
            }
        }
    }

    // ── Complete / Fail / Next ────────────────────────────────────────────────

    private fun completeGoal() {
        stopTimer()
        _stateFlow.value = State.COMPLETED
        playerModel.addCoins(currentGoal?.reward ?: 0L)
        GlobalEvents.emit(GlobalEvents.EventType.GOAL_COMPLETED)
        scope.launch { delay(2000); nextGoal() }

        currentGoal?.let { goal -> gdxGame.analytics.goalCompleted(goal.typeName, goal.reward) }
    }

    private fun failGoal() {
        stopTimer()
        _stateFlow.value = State.FAILED
        GlobalEvents.emit(GlobalEvents.EventType.GOAL_FAILED)
        scope.launch { delay(1800); nextGoal() }

        currentGoal?.let { goal -> gdxGame.analytics.goalFailed(goal.typeName) }
    }

    private fun nextGoal() {
        val lastGoal = currentGoal

        _goalCounterFlow.value++
        state.goalState = GoalState(counter = _goalCounterFlow.value)  // зберігаємо тільки counter

        val next = generateAndSave(lastGoal)
        _currentGoalFlow.value = next
        _stateFlow.value       = State.ACTIVE
        refreshProgress()

        if (next is Goal.Timed) startTimer(next.timeLimitSec)

        GlobalEvents.emit(GlobalEvents.EventType.GOAL_CHANGED)
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private var timerJob: Job? = null

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        _timerFlow.value = seconds
        timerJob = scope.launch {
            while (_timerFlow.value > 0) {
                delay(1000)
                _timerFlow.value--
                state.goalState = state.goalState.copy(timerRemaining = _timerFlow.value)
            }
            failGoal()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerFlow.value = 0
        state.goalState = state.goalState.copy(timerRemaining = 0)
    }

    // ── Progress ──────────────────────────────────────────────────────────────

    private fun refreshProgress() {
        val goal = currentGoal ?: run { _progressFlow.value = null; return }
        val grid = state.gridFlow.value

        _progressFlow.value = when (goal) {
            is Goal.Simple   -> {
                val maxCube = grid.filter { it > 0 }.maxOrNull() ?: 0
                GoalProgress.Simple(maxCube, goal.targetLevel)
            }
            is Goal.Combined -> buildCombinedProgress(goal.requirements, grid)
            is Goal.Timed    -> buildCombinedProgress(goal.requirements, grid)
        }
    }

    private fun buildCombinedProgress(
        reqs: List<Goal.Combined.Requirement>,
        grid: List<Int>,
    ): GoalProgress.Combined = GoalProgress.Combined(
        reqs.map { req ->
            GoalProgress.Combined.Item(
                level    = req.level,
                current  = grid.count { it == req.level }.coerceAtMost(req.count),
                required = req.count,
            )
        }
    )

    // ── Generation ────────────────────────────────────────────────────────────

    private fun generateAndSave(lastGoal: Goal? = null): Goal {
        val grid = state.gridFlow.value
        val ctx  = GoalContext(
            maxCube     = grid.filter { it > 0 }.maxOrNull() ?: 1,
            buyLevel    = buyLevelModel.currentBuyLevel,
            playerLevel = playerModel.currentLevel,
        )
        return GoalGenerator.generate(lastGoal, ctx).also { saveToState(it) }
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    private fun restoreFromState(): Goal? {
        val gs = state.goalState
        return Goal.fromState(
            typeName     = gs.typeName,
            reward       = gs.reward,
            targetLevel  = gs.targetLevel,
            requirements = gs.requirements.map { Goal.Combined.Requirement(it.level, it.count) },
            timeLimitSec = gs.timeLimitSec,
        )
    }

    private fun saveToState(goal: Goal) {
        state.goalState = GoalState(
            typeName      = goal.typeName,
            reward        = goal.reward,
            targetLevel   = if (goal is Goal.Simple) goal.targetLevel  else 0,
            timeLimitSec  = if (goal is Goal.Timed)  goal.timeLimitSec else 0,
            requirements  = when (goal) {
                is Goal.Combined -> goal.requirements.map { GoalRequirement(it.level, it.count) }
                is Goal.Timed    -> goal.requirements.map { GoalRequirement(it.level, it.count) }
                else             -> emptyList()
            },
            timerRemaining = 0,
            counter        = _goalCounterFlow.value,
        )
    }
}