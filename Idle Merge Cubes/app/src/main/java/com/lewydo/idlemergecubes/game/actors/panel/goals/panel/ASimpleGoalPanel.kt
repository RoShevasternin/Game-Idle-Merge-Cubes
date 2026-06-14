package com.lewydo.idlemergecubes.game.actors.panel.goals.panel

import com.lewydo.idlemergecubes.game.actors.panel.goals.body.AReachLevelBody
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ASimpleGoalPanel — зелена панель, objective = ReachLevel
//
//   [SIMPLE] #N                                   [🪙 reward]
//   [Cube N] Reach this cube level                       5/6
//            [============------------]
// ═════════════════════════════════════════════════════════════════════════════

class ASimpleGoalPanel(override val screen: AdvancedScreen) : AGoalPanelBase(screen) {

    // ------------------------------------------------------------------------
    // Config
    // ------------------------------------------------------------------------
    override val bgTexture  = gdxGame.assetsAll.BG_SIMPLE
    override val badgeText  = "SIMPLE"
    override val badgeColor = GameColor.green_66

    // ------------------------------------------------------------------------
    // Body
    // ------------------------------------------------------------------------
    private val aReachBody = AReachLevelBody(screen)

    // ------------------------------------------------------------------------
    // Add Body
    // ------------------------------------------------------------------------
    override fun addBody() {
        aReachBody.setSize(1810f, 247f)
        add(aReachBody) { centerX(); bottomToBottom() }
    }

    // ------------------------------------------------------------------------
    // Bind
    // ------------------------------------------------------------------------
    override fun bindObjective(goal: Goal) {
        (goal.objective as? GoalObjective.ReachLevel)?.let {
            aReachBody.setDescription("Reach this cube level")
            aReachBody.setObjective(it)
        }
    }

    override fun bindProgress(progress: GoalProgress) {
        (progress as? GoalProgress.ReachLevel)?.let { aReachBody.setProgress(it) }
    }

}