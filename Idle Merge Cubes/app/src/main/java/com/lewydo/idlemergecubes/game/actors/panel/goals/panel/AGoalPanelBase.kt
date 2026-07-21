package com.lewydo.idlemergecubes.game.actors.panel.goals.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.goals.util.AGoalsBadge
import com.lewydo.idlemergecubes.game.actors.panel.goals.util.AGoalsReward
import com.lewydo.idlemergecubes.game.actors.panel.goals.util.AGoalsTimer
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  AGoalPanelBase — спільний каркас панелі задачі
//
//  Кожен тип задачі (Simple/Combined/Timed) — ОКРЕМИЙ актор-нащадок.
//  База тримає лише те, що структурно спільне:
//    фон • бейдж (текст+колір) • #N • reward (coin+num) • опц. таймер
//
//  Нащадок задає кольори/текст бейджа/фон, наявність таймера та власне ТІЛО:
//    addBody()       — додати контент під header
//    bindObjective() — заповнити тіло даними задачі
//    bindProgress()  — оновити прогрес тіла
//
//   ┌────────────────────────────────────────────────────────────┐
//   │ [BADGE] #N                      [⏱ 30s]?   [🪙 reward]      │  ← база
//   │  <тіло нащадка>                                             │  ← addBody()
//   └────────────────────────────────────────────────────────────┘
//
//  ⚠ Фон і бейдж зараз — tinted panel_coin / solid texture. Заміни на
//     дизайнерські текстури (panel_goal_simple/combined/timed, badge_*).
// ═════════════════════════════════════════════════════════════════════════════

abstract class AGoalPanelBase(screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Config (нащадок задає)
    // ------------------------------------------------------------------------
    protected abstract val bgTexture : Texture
    protected abstract val badgeText : String
    protected abstract val badgeColor: Color
    protected open     val hasTimer  : Boolean = false

    // ------------------------------------------------------------------------
    // Color
    // ------------------------------------------------------------------------
    private val TIMER_NORMAL = gdxGame.assetsAll.goals_pill_timer
    private val TIMER_URGENT = gdxGame.assetsAll.goals_pill_timer_red

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleBadge  = MsdfStyle(msdf, msdf.fontNunitoBold, 48f)
    private val styleReward = styleBadge.copy(size = 56f)
    private val styleIndex  = MsdfStyle(msdf, msdf.fontNunitoRegular, 48f)
    private val styleTimer  = MsdfStyle(msdf, msdf.fontNunitoSemiBold, 56f)

    // ------------------------------------------------------------------------
    // Actors — Header
    // ------------------------------------------------------------------------
    private val aBg          = Image()
    private val aGoalsBadge  = AGoalsBadge(screen, styleBadge)
    private val aIndexLbl    = AMsdfLabel("#1", styleIndex)
    private val aGoalsReward = AGoalsReward(screen, styleReward)
    private val aGoalsTimer  = AGoalsTimer(screen, styleTimer)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    final override fun addActorsOnGroup() {
        addBg()
        addHeader()
        addBody()
    }

    // ------------------------------------------------------------------------
    // Add Actors — Background
    // ------------------------------------------------------------------------

    private fun addBg() {
        aBg.drawable = TextureRegionDrawable(bgTexture)
        add(aBg) { fillParent() }
    }

    // ------------------------------------------------------------------------
    // Add Actors — Header
    // ------------------------------------------------------------------------

    private fun addHeader() {
        // Badge pill
        aGoalsBadge.setBgColor(badgeColor.cpy())
        aGoalsBadge.setSize(1f, 81f)
        add(aGoalsBadge) { startToStart(margin = 38f); topToTop(margin = 35f) }
        aGoalsBadge.setText(badgeText)

        // Index "#N"
        aIndexLbl.setSize(1f, 65f)
        add(aIndexLbl) { startToEnd(aGoalsBadge, margin = 18f); centerY(aGoalsBadge) }

        // Reward (rightmost)
        aGoalsReward.setSize(1f, 96f)
        add(aGoalsReward) { endToEnd(margin = 37f); centerY(aGoalsBadge) }

        // Timer (only timed)
        if (hasTimer) {
            aGoalsTimer.setBg(TIMER_NORMAL)
            aGoalsTimer.setSize(1f, 96f)
            add(aGoalsTimer) { endToStart(aGoalsReward, margin = 28f); centerY(aGoalsReward) }
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors — Body (нащадок)
    // ------------------------------------------------------------------------

    protected abstract fun addBody()

    // ════════════════════════════════════════════════════════════════════════
    //  bind* — викликає GoalsController / APanelGoals
    // ════════════════════════════════════════════════════════════════════════

    open fun bindGoal(goal: Goal) {
        aGoalsReward.setText(NumberFormatter.format(goal.reward))
        bindObjective(goal)
    }

    protected abstract fun bindObjective(goal: Goal)

    abstract fun bindProgress(progress: GoalProgress)

    fun bindCounter(counter: Int) {
        aIndexLbl.setText("#$counter")
        aIndexLbl.pack()
    }

    open fun bindTimer(seconds: Int) {
        if (!hasTimer) return
        aGoalsTimer.setText("${seconds}s")

        val isUrgent = seconds in 1..10
        aGoalsTimer.setTextColor(if (isUrgent) Color.BLACK else Color.WHITE)
        aGoalsTimer.setBg(if (isUrgent) TIMER_URGENT else TIMER_NORMAL)
        if (isUrgent) animTimerPulse()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private var timerPulseRunning = false
    private fun animTimerPulse() {
        if (timerPulseRunning) return
        timerPulseRunning = true
        aGoalsTimer.clearActions()
        aGoalsTimer.setOrigin(Align.center)
        aGoalsTimer.addAction(
            Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, 0.12f, Interpolation.sineOut),
            Actions.scaleTo(1.0f,  1.0f,  0.12f, Interpolation.sineOut),
            Actions.run { timerPulseRunning = false }
        ))
    }

}