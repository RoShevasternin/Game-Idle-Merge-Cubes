package com.lewydo.idlemergecubes.game.actors.panel.goals.body

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.layout.autoLayout.AAutoLayout
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.goals.util.AGoalRequirementItem
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACollectBody — контент для GoalObjective.Collect
//
//   Place on the board:
//   [4] 1/1✓   [5] 0/4   [2] 0/2
//
//   MAX_REQ - максимум 4 Item задачі.
//
//  Перевикористовується: ACombinedGoalPanel + ATimedGoalPanel.
//  Опис задає панель (різний для timed). Дамб-view.
// ═════════════════════════════════════════════════════════════════════════════

class ACollectBody(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Sizes
    // ------------------------------------------------------------------------
    private val MAX_REQ = 4

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoRegular, 48f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDescLbl  = AMsdfLabel("Place on the board:", styleDef)
    private val aReqItems = List(MAX_REQ) { AGoalRequirementItem(screen) }

    private val aHorizontal = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        sizingW   = AAutoLayout.Sizing.HUG,
        gapMain   = 32f,
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addDescLbl()
        addHorizontal()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDescLbl() {
        aDescLbl.setSize(415f, 65f)
        add(aDescLbl) { startToStart(margin = 38f); topToTop() }
    }

    private fun addHorizontal() {
        aHorizontal.setSize(1f, 125f)
        add(aHorizontal) { startToStart(margin = 38f); bottomToBottom(margin = 30f) }

        aHorizontal.addReqItems()
    }

    private fun AAutoLayout.addReqItems() {
        aReqItems.forEach { item -> item.setSize(1f, 125f); add(item) }
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    fun setDescription(text: String) {
        aDescLbl.setText(text)
    }

    fun setObjective(objective: GoalObjective.Collect) {
        aReqItems.forEachIndexed { i, item ->
            val req = objective.requirements.getOrNull(i)
            item.isVisible = req != null
            if (req != null) item.setRequirement(req.level, req.count)
        }
    }

    fun setProgress(progress: GoalProgress.Collect) {
        progress.items.forEachIndexed { i, item ->
            aReqItems.getOrNull(i)?.updateProgress(item.current, item.required)
        }
    }

}