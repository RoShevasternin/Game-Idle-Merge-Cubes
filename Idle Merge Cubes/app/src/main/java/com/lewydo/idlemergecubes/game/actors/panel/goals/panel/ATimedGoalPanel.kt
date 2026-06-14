package com.lewydo.idlemergecubes.game.actors.panel.goals.panel

import com.badlogic.gdx.graphics.Color
import com.lewydo.idlemergecubes.game.actors.panel.goals.body.ACollectBody
import com.lewydo.idlemergecubes.game.actors.panel.goals.body.AReachLevelBody
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ATimedGoalPanel — червона панель з таймером
//
//  Окремий актор. objective може бути будь-якої форми, тому панель тримає
//  ОБИДВА тіла й показує потрібне:
//    ReachLevel → aReachBody   ("timed як simple")
//    Collect    → aCollectBody ("timed як combined")
//
//  Якщо потім зʼявиться інша timed-логіка — додаєш ще одне тіло сюди.
//
//   [TIMED] #N                          [⏱ 12s]   [🪙 reward]
//   Place on the board in Time:
//   [4] 1/1✓   [5] 0/6   [2] 0/6
// ═════════════════════════════════════════════════════════════════════════════

class ATimedGoalPanel(override val screen: AdvancedScreen) : AGoalPanelBase(screen) {

    // ------------------------------------------------------------------------
    // Config
    // ------------------------------------------------------------------------
    override val bgTexture  = gdxGame.assetsAll.BG_TIMED
    override val badgeText  = "TIMED"
    override val badgeColor = GameColor.red_E22057
    override val hasTimer   = true

    // ------------------------------------------------------------------------
    // Bodies — показуємо одне залежно від objective
    // ------------------------------------------------------------------------
    private val aReachBody   = AReachLevelBody(screen)
    private val aCollectBody = ACollectBody(screen)

    // ------------------------------------------------------------------------
    // Add Body
    // ------------------------------------------------------------------------
    override fun addBody() {
        aReachBody.setSize(1810f, 247f)
        add(aReachBody) { centerX(); bottomToBottom() }

        aCollectBody.setSize(1810f, 247f)
        add(aCollectBody) { centerX(); bottomToBottom() }

        aReachBody.isVisible   = false
        aCollectBody.isVisible = false
    }

    // ------------------------------------------------------------------------
    // Bind
    // ------------------------------------------------------------------------
    override fun bindObjective(goal: Goal) {
        when (val obj = goal.objective) {
            is GoalObjective.ReachLevel -> {
                aCollectBody.setDescription("Reach this cube level in Time:")
                aReachBody.setObjective(obj)
                aReachBody.isVisible   = true
                aCollectBody.isVisible = false
            }
            is GoalObjective.Collect -> {
                aCollectBody.setDescription("Place on the board in Time:")
                aCollectBody.setObjective(obj)
                aReachBody.isVisible   = false
                aCollectBody.isVisible = true
            }
        }
    }

    override fun bindProgress(progress: GoalProgress) {
        when (progress) {
            is GoalProgress.ReachLevel -> aReachBody.setProgress(progress)
            is GoalProgress.Collect    -> aCollectBody.setProgress(progress)
        }
    }

}