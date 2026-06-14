package com.lewydo.idlemergecubes.game.actors.panel.goals

import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.actors.panel.goals.overlay.AGoalResultOverlay
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.ACombinedGoalPanel
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.AGoalPanelBase
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.ASimpleGoalPanel
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.ATimedGoalPanel
import com.lewydo.idlemergecubes.game.controller.GoalsController
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  APanelGoals — ОРКЕСТРАТОР панелей задач
//
//  Тримає три самостійні панелі-актори + спільний result overlay.
//  Залежно від goal.category показує одну панель:
//    SIMPLE   → ASimpleGoalPanel
//    COMBINED → ACombinedGoalPanel
//    TIMED    → ATimedGoalPanel
//
//  Шари:
//  GoalsModel (flows) → GoalsController → APanelGoals → активна панель → StateMachine → result overlay
//
//  Контролер кличе bind*()  — оновити дані активної панелі.
//  Стани   кличуть show*()  — overlay completed/failed/active.
// ═════════════════════════════════════════════════════════════════════════════

class APanelGoals(override val screen: AdvancedScreen) : AConstraintLayout(screen) {



    // ------------------------------------------------------------------------
    // TODO: DEBUG — циклічне перемикання типів goal по кліку (ПРИБРАТИ В РЕЛІЗІ)
    // ------------------------------------------------------------------------
    private val debugGoals = listOf(
        Goal(GoalObjective.ReachLevel(12), reward = 3000000),
        Goal(GoalObjective.Collect(listOf(
            GoalObjective.Collect.Requirement(4, 1),
            GoalObjective.Collect.Requirement(500, 6000),
            GoalObjective.Collect.Requirement(15, 40),
        )), reward = 3),
        Goal(GoalObjective.Collect(listOf(
            GoalObjective.Collect.Requirement(4, 1),
            GoalObjective.Collect.Requirement(5, 6),
            GoalObjective.Collect.Requirement(2, 6),
        )), reward = 300, timeLimitSec = 15),
        Goal(GoalObjective.ReachLevel(50), reward = 50, timeLimitSec = 15),
    )
    private var debugIndex = 0
    private var debugTimerLeft = 0

    private fun enableDebugCycle() {
        addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                val goal = debugGoals[debugIndex % debugGoals.size]
                debugIndex++

                bindGoal(goal)
                bindCounter(debugIndex)
                bindProgress(debugProgressFor(goal))
                hideResultOverlay()

                startDebugTimer(goal)
            }
        })
    }

    private fun startDebugTimer(goal: Goal) {
        clearActions()                         // зупиняємо попередній відлік
        if (!goal.isTimed) return

        debugTimerLeft = goal.timeLimitSec!!   // 15
        bindTimer(debugTimerLeft)

        addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.forever(
            com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(1f,
                com.badlogic.gdx.scenes.scene2d.actions.Actions.run {
                    debugTimerLeft--
                    bindTimer(debugTimerLeft.coerceAtLeast(0))
                    if (debugTimerLeft <= 0) {
                        clearActions()
                        showFailedOverlay()    // показати екран провалу
                    }
                }
            )
        ))
    }

    private fun debugProgressFor(goal: Goal): GoalProgress = when (val o = goal.objective) {
        is GoalObjective.ReachLevel -> GoalProgress.ReachLevel(current = 6, target = o.targetLevel)
        is GoalObjective.Collect    -> GoalProgress.Collect(o.requirements.map {
            GoalProgress.Collect.Item(it.level, current = if (it.level == 4) 1 else 0, required = it.count)
        })
    }
    // ------------------------------------------------------------------------
    // TODO: DEBUG — циклічне перемикання типів goal по кліку (ПРИБРАТИ В РЕЛІЗІ)
    // ------------------------------------------------------------------------




    // ------------------------------------------------------------------------
    // Panels & Overlay
    // ------------------------------------------------------------------------
    private val aSimplePanel   = ASimpleGoalPanel(screen)
    private val aCombinedPanel = ACombinedGoalPanel(screen)
    private val aTimedPanel    = ATimedGoalPanel(screen)

    private val aResultOverlay = AGoalResultOverlay(screen)

    private val allPanels = listOf(aSimplePanel, aCombinedPanel, aTimedPanel)

    // ------------------------------------------------------------------------
    // Controller & State
    // ------------------------------------------------------------------------
    private val controller by lazy { GoalsController(gdxGame.modelGoals, this, coroutine!!) }

    private var activePanel: AGoalPanelBase? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanels()
        addResultOverlay()

        allPanels.forEach { it.isVisible = false }

        //controller.bind()
        enableDebugCycle()   //TODO: ← DEBUG: прибрати в релізі
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addPanels() {
        allPanels.forEach { panel ->
            add(panel) { fillParent() }
        }
    }

    private fun addResultOverlay() {
        add(aResultOverlay) { fillParent() }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  bind* — викликає GoalsController (оновлення даних)
    // ════════════════════════════════════════════════════════════════════════

    fun bindGoal(goal: Goal) {
        val panel = panelFor(goal)
        activePanel = panel

        panel.bindGoal(goal)
        showOnlyActivePanel()
    }

    fun bindProgress(progress: GoalProgress) {
        activePanel?.bindProgress(progress)
    }

    fun bindTimer(seconds: Int) {
        activePanel?.bindTimer(seconds)
    }

    fun bindCounter(counter: Int) {
        activePanel?.bindCounter(counter)
    }

    private fun panelFor(goal: Goal): AGoalPanelBase = when (goal.category) {
        Goal.Category.SIMPLE   -> aSimplePanel
        Goal.Category.COMBINED -> aCombinedPanel
        Goal.Category.TIMED    -> aTimedPanel
    }

    private fun showOnlyActivePanel() {
        allPanels.forEach { it.isVisible = it === activePanel }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  show* — викликають стани (overlay)
    // ════════════════════════════════════════════════════════════════════════

    fun hideResultOverlay() {
        aResultOverlay.hideImmediate()
    }

    fun showCompletedOverlay(reward: Long) {
        aResultOverlay.showCompleted(reward)
    }

    fun showFailedOverlay() {
        aResultOverlay.showFailed()
    }

}