package com.lewydo.idlemergecubes.game.actors.panel.goals

import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.actors.panel.goals.overlay.AGoalResultOverlay
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.ACombinedGoalPanel
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.AGoalPanelBase
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.ASimpleGoalPanel
import com.lewydo.idlemergecubes.game.actors.panel.goals.panel.ATimedGoalPanel
import com.lewydo.idlemergecubes.game.controller.GoalsController
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image

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
    // Panels & Overlay
    // ------------------------------------------------------------------------
    private val aSimplePanel   = ASimpleGoalPanel(screen)
    private val aCombinedPanel = ACombinedGoalPanel(screen)
    private val aTimedPanel    = ATimedGoalPanel(screen)

    private val aResultOverlay = AGoalResultOverlay(screen)

    private val allPanels = listOf(aSimplePanel, aCombinedPanel, aTimedPanel)

    // ------------------------------------------------------------------------
    // Reward coins pool — переюзабельні монетки для польоту в баланс
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // Reward coins — монетки летять у баланс при completed (на stage, поверх усього)
    // ------------------------------------------------------------------------
    private val COIN_MIN_COUNT = 10
    private val COIN_MAX_COUNT = 15
    private val COIN_MIN_SIZE  = 60f
    private val COIN_MAX_SIZE  = 100f

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

        controller.bind()
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
        panel.bindCounter(lastCounter)   // застосовуємо актуальний номер одразу
        showOnlyActivePanel()
    }

    fun bindProgress(progress: GoalProgress) {
        activePanel?.bindProgress(progress)
    }

    fun bindTimer(seconds: Int) {
        activePanel?.bindTimer(seconds)
    }

    private var lastCounter = 1

    fun bindCounter(counter: Int) {
        lastCounter = counter            // кешуємо — застосується і при наступному bindGoal
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
        gdxGame.soundUtil.apply { play(GOALS_DONE) }
        aResultOverlay.showCompleted(reward)
        flyRewardCoins()
    }

    fun showFailedOverlay() {
        gdxGame.soundUtil.apply { play(GOALS_FAIL) }
        aResultOverlay.showFailed()
    }

    // ------------------------------------------------------------------------
    // Flying coins → balance
    // ------------------------------------------------------------------------
    //
    // При completed монетки створюються на stageUI.root (поверх УСЬОГО),
    // розлітаються з центру панелі й летять у баланс, у кінці self-remove.
    // Лише ВІЗУАЛ — нарахування робить GoalsModel.completeGoal.
    // END_FLY_COIN тригерить shake балансу.

    private fun flyRewardCoins() {
        val target = GlobalStagePositions.get(GlobalStagePositions.Key.COIN)
        if (target == Vector2.Zero) return

        // центр панелі у stage-координатах (монетки живуть на stage)
        val center = localToStageCoordinates(Vector2(width / 2f, height / 2f))

        val count = (COIN_MIN_COUNT..COIN_MAX_COUNT).random()

        // фази таймінгу
        val scatterTime = 0.30f   // розліт по панелі
        val restTime    = 0.55f   // лежать (юзер бачить купку)
        val flyTime     = 0.40f   // політ однієї монетки в баланс
        val flyStagger  = 0.07f   // інтервал між відльотами (по одній)

        repeat(count) { i ->
            val size = COIN_MIN_SIZE + Math.random().toFloat() * (COIN_MAX_SIZE - COIN_MIN_SIZE)

            val coin = Image(gdxGame.assetsAll.coin)
            coin.setSize(size, size)
            coin.setOrigin(size / 2f, size / 2f)
            coin.setScale(0f)
            // старт — щільна купка в центрі
            coin.setPosition(center.x - size / 2f, center.y - size / 2f)
            screen.stageUI.root.addActor(coin)

            // куди розлетітись по панелі (навколо центру)
            val scatterX = center.x + (Math.random().toFloat() * 320f - 160f)
            val scatterY = center.y + (Math.random().toFloat() * 150f - 50f)

            coin.addAction(Actions.sequence(
                Actions.delay(i * 0.025f),                         // купка висипається майже разом
                // 1) поява + розліт по панелі (дугою — трохи вгору)
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, scatterTime, Interpolation.swingOut),
                    Actions.moveTo(scatterX - size / 2f, scatterY - size / 2f, scatterTime, Interpolation.sineOut),
                ),
                // 2) лежать — кожна свій час, щоб відліт був "по одній"
                Actions.delay(restTime + i * flyStagger),
                // 3) політ у баланс
                Actions.parallel(
                    Actions.moveTo(target.x - size / 2f, target.y - size / 2f, flyTime, Interpolation.swingIn),
                    Actions.scaleTo(0.5f, 0.5f, flyTime, Interpolation.sineIn),
                ),
                // 4) поштовх балансу на прильоті
                Actions.run { GlobalEvents.emit(GlobalEvents.EventType.END_FLY_COIN) },
                // 5) self-remove зі stage
                Actions.removeActor(),
            ))
        }
    }

}