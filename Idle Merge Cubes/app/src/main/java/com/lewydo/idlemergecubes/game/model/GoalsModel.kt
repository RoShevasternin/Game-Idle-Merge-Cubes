package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.data.GoalRequirement
import com.lewydo.idlemergecubes.game.data.GoalState
import com.lewydo.idlemergecubes.game.state.GameState
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalContext
import com.lewydo.idlemergecubes.game.systems.goals.GoalGenerator
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
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
import kotlin.time.Duration.Companion.milliseconds

class GoalsModel(
    private val state        : GameState,
    private val playerModel  : PlayerModel,
    private val buyLevelModel: BuyLevelModel,
    private val scope        : CoroutineScope,
) {

    // ------------------------------------------------------------------------
    // State enum
    // ------------------------------------------------------------------------

    enum class State { ACTIVE, COMPLETED, FAILED }

    // ------------------------------------------------------------------------
    // Public flows
    // ------------------------------------------------------------------------

    private val _currentGoalFlow = MutableStateFlow<Goal?>(null)
    private val _progressFlow    = MutableStateFlow<GoalProgress?>(null)
    private val _stateFlow       = MutableStateFlow(State.ACTIVE)
    private val _timerFlow       = MutableStateFlow(0)
    private val _goalCounterFlow = MutableStateFlow(1)

    val currentGoalFlow : StateFlow<Goal?>         = _currentGoalFlow.asStateFlow()
    val progressFlow    : StateFlow<GoalProgress?> = _progressFlow.asStateFlow()
    val stateFlow       : StateFlow<State>         = _stateFlow.asStateFlow()
    val timerFlow       : StateFlow<Int>           = _timerFlow.asStateFlow()
    val goalCounterFlow : StateFlow<Int>           = _goalCounterFlow.asStateFlow()

    val currentGoal: Goal? get() = _currentGoalFlow.value

    // ------------------------------------------------------------------------
    // Auto-init
    // ------------------------------------------------------------------------

    init {
        scope.launch {
            // Чекаємо ПОВНОГО завантаження збереженого стану (goalState + grid),
            // інакше restoreFromState прочитає дефолтний goalState → race.
            state.isLoadedFlow.first { it }
            state.gridFlow.first { it.size == 16 }
            initGoal()
            observeReachability()
        }
    }

    // ------------------------------------------------------------------------
    // Reachability — задача могла стати недосяжною після росту buyLevel
    // ------------------------------------------------------------------------
    //
    // Приклад: задача "зібрати 5 кубів рівня 3" видана при buyLevel=3.
    // Гравець доростив дошку → buyLevel став 4 → кнопка BUY дає лише рівень 4,
    // а рівень 3 більше ніяк не отримати (зі злиття виходять ЛИШЕ вищі рівні).
    // Якщо потрібних кубів уже немає на полі — задача залипне назавжди → fail.

    private fun observeReachability() {
        scope.launch {
            buyLevelModel.buyLevelFlow.collect {
                if (_stateFlow.value == State.ACTIVE && !isGoalReachable()) {
                    failGoal()   // нездійсненна → провал + генерація нової
                }
            }
        }
    }

    private fun isGoalReachable(): Boolean {
        val goal = currentGoal ?: return true
        val obj  = goal.objective
        if (obj !is GoalObjective.Collect) return true   // ReachLevel завжди досяжний (мержимо вгору)

        val grid     = state.gridFlow.value
        val buyLevel = buyLevelModel.currentBuyLevel

        // Вимога недосяжна, якщо її рівень нижчий за buyLevel І на полі вже
        // недостатньо таких кубів (доукомплектувати нізвідки).
        return obj.requirements.none { req ->
            req.level < buyLevel && grid.count { it == req.level } < req.count
        }
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    fun checkCompletion() {
        if (_stateFlow.value != State.ACTIVE) return
        refreshProgress()
        if (_progressFlow.value?.isDone == true) completeGoal()
    }

    fun pauseTimer() {
        // Паузимо лише активний timed-goal, що реально йде
        val goal = currentGoal ?: return
        if (!goal.isTimed || _stateFlow.value != State.ACTIVE) return
        if (timerJob == null) return

        timerJob?.cancel()
        timerJob = null
        timerPaused = true
        // remaining уже збережено в goalState на кожному тіку (див. startTimer)
        state.goalState = state.goalState.copy(timerRemaining = _timerFlow.value)
    }

    fun resumeTimer() {
        val goal = currentGoal ?: return
        if (!goal.isTimed || _stateFlow.value != State.ACTIVE) return
        if (!timerPaused) return            // не на паузі — нічого не робимо
        if (timerJob != null) return        // вже йде

        timerPaused = false
        val remaining = _timerFlow.value
        if (remaining > 0) startTimer(remaining) else failGoal()
    }

    // ------------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------------

    private fun initGoal() {
        _goalCounterFlow.value = state.goalState.counter

        val restored = restoreFromState()
        val goal     = restored ?: generateAndSave()

        _currentGoalFlow.value = goal
        _stateFlow.value       = State.ACTIVE
        refreshProgress()

        // Відновлена задача могла стати недосяжною (buyLevel виріс) → нова
        if (restored != null && !isGoalReachable()) {
            failGoal()
            return
        }

        if (goal.isTimed) {
            val remaining = state.goalState.timerRemaining
            when {
                remaining > 0    -> startTimer(remaining)
                restored != null -> failGoal()                 // timed відновлено без часу → провал
                else             -> startTimer(goal.timeLimitSec!!)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Complete / Fail / Next
    // ------------------------------------------------------------------------

    private fun completeGoal() {
        if (_stateFlow.value != State.ACTIVE) return
        stopTimer()
        _stateFlow.value = State.COMPLETED
        playerModel.addCoins(currentGoal?.reward ?: 0L)
        GlobalEvents.emit(GlobalEvents.EventType.GOAL_COMPLETED)
        scope.launch { delay(2000.milliseconds); nextGoal() }

        currentGoal?.let { gdxGame.analytics.goalCompleted(it.category.name.lowercase(), it.reward) }
    }

    private fun failGoal() {
        if (_stateFlow.value != State.ACTIVE) return
        stopTimer()
        _stateFlow.value = State.FAILED
        GlobalEvents.emit(GlobalEvents.EventType.GOAL_FAILED)
        scope.launch { delay(1800.milliseconds); nextGoal() }

        currentGoal?.let { gdxGame.analytics.goalFailed(it.category.name.lowercase()) }
    }

    private fun nextGoal() {
        val lastGoal = currentGoal

        _goalCounterFlow.value++
        state.goalState = GoalState(counter = _goalCounterFlow.value)

        val next = generateAndSave(lastGoal)
        _currentGoalFlow.value = next
        _stateFlow.value       = State.ACTIVE
        refreshProgress()

        if (next.isTimed) startTimer(next.timeLimitSec!!)

        GlobalEvents.emit(GlobalEvents.EventType.GOAL_CHANGED)
    }

    // ------------------------------------------------------------------------
    // Timer
    // ------------------------------------------------------------------------

    private var timerJob: Job? = null
    private var timerPaused = false

    private fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerPaused = false
        _timerFlow.value = seconds
        timerJob = scope.launch {
            while (_timerFlow.value > 0) {
                delay(1000.milliseconds)
                _timerFlow.value--
                state.goalState = state.goalState.copy(timerRemaining = _timerFlow.value)
            }
            failGoal()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        timerPaused = false
        _timerFlow.value = 0
        state.goalState = state.goalState.copy(timerRemaining = 0)
    }

    // ------------------------------------------------------------------------
    // Progress
    // ------------------------------------------------------------------------

    private fun refreshProgress() {
        val goal = currentGoal ?: run { _progressFlow.value = null; return }
        val grid = state.gridFlow.value

        _progressFlow.value = when (val obj = goal.objective) {
            is GoalObjective.ReachLevel -> {
                val maxCube = grid.filter { it > 0 }.maxOrNull() ?: 0
                GoalProgress.ReachLevel(maxCube, obj.targetLevel)
            }
            is GoalObjective.Collect -> buildCollectProgress(obj.requirements, grid)
        }
    }

    private fun buildCollectProgress(
        reqs: List<GoalObjective.Collect.Requirement>,
        grid: List<Int>,
    ): GoalProgress.Collect = GoalProgress.Collect(
        reqs.map { req ->
            GoalProgress.Collect.Item(
                level    = req.level,
                current  = grid.count { it == req.level }.coerceAtMost(req.count),
                required = req.count,
            )
        }
    )

    // ------------------------------------------------------------------------
    // Generation
    // ------------------------------------------------------------------------

    private fun generateAndSave(lastGoal: Goal? = null): Goal {
        val grid = state.gridFlow.value
        val ctx  = GoalContext(
            maxCube = grid.filter { it > 0 }.maxOrNull() ?: 1,
            buyLevel = buyLevelModel.currentBuyLevel,
            playerLevel = playerModel.currentLevel,
        )
        return GoalGenerator.generate(lastGoal, ctx).also { saveToState(it) }
    }

    // ------------------------------------------------------------------------
    // Persistence
    // ------------------------------------------------------------------------
    //
    // GoalState лишається сумісним зі старим форматом:
    //   typeName     ← objective::simpleName ("ReachLevel" | "Collect")
    //   targetLevel  ← ReachLevel.targetLevel
    //   requirements ← Collect.requirements
    //   timeLimitSec ← Goal.timeLimitSec (0 = не timed)

    private fun restoreFromState(): Goal? {
        val gs = state.goalState
        if (gs.typeName.isBlank()) return null

        val objective: GoalObjective = when (gs.typeName) {
            GoalObjective.ReachLevel::class.simpleName ->
                GoalObjective.ReachLevel(gs.targetLevel)
            GoalObjective.Collect::class.simpleName ->
                GoalObjective.Collect(gs.requirements.map { GoalObjective.Collect.Requirement(it.level, it.count) })
            else -> return null
        }

        val timeLimit = gs.timeLimitSec.takeIf { it > 0 }
        return Goal(objective = objective, reward = gs.reward, timeLimitSec = timeLimit)
    }

    private fun saveToState(goal: Goal) {
        val obj = goal.objective
        state.goalState = GoalState(
            typeName       = obj.typeName,
            reward         = goal.reward,
            targetLevel    = (obj as? GoalObjective.ReachLevel)?.targetLevel ?: 0,
            timeLimitSec   = goal.timeLimitSec ?: 0,
            requirements   = (obj as? GoalObjective.Collect)?.requirements
                ?.map { GoalRequirement(it.level, it.count) } ?: emptyList(),
            // timed → стартуємо з повного часу (а не 0), щоб restore щойно
            // згенерованої timed-задачі не провалив її через timerRemaining==0
            timerRemaining = goal.timeLimitSec ?: 0,
            counter        = _goalCounterFlow.value,
        )
    }
}