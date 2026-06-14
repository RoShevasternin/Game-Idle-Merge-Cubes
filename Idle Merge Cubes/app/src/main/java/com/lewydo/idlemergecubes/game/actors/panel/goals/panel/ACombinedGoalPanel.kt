package com.lewydo.idlemergecubes.game.actors.panel.goals.panel

import com.lewydo.idlemergecubes.game.actors.panel.goals.body.ACollectBody
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACombinedGoalPanel — синя панель, objective = Collect (без прогрес-бару)
//
//   [COMBINED] #N                                 [🪙 reward]
//   Place on the board:
//   [4] 1/1✓   [5] 0/6   [2] 0/6
// ═════════════════════════════════════════════════════════════════════════════

class ACombinedGoalPanel(override val screen: AdvancedScreen) : AGoalPanelBase(screen) {

    // ------------------------------------------------------------------------
    // Config
    // ------------------------------------------------------------------------
    override val bgTexture  = gdxGame.assetsAll.BG_COMBINED
    override val badgeText  = "COMBINED"
    override val badgeColor = GameColor.blue_3A44FF

    // ------------------------------------------------------------------------
    // Body
    // ------------------------------------------------------------------------
    private val aCollectBody = ACollectBody(screen)

    // ------------------------------------------------------------------------
    // Add Body
    // ------------------------------------------------------------------------
    override fun addBody() {
        aCollectBody.setSize(1810f, 247f)
        add(aCollectBody) { centerX(); bottomToBottom() }
    }

    // ------------------------------------------------------------------------
    // Bind
    // ------------------------------------------------------------------------
    override fun bindObjective(goal: Goal) {
        (goal.objective as? GoalObjective.Collect)?.let {
            aCollectBody.setDescription("Place on the board:")
            aCollectBody.setObjective(it)
        }
    }

    override fun bindProgress(progress: GoalProgress) {
        (progress as? GoalProgress.Collect)?.let { aCollectBody.setProgress(it) }
    }

}