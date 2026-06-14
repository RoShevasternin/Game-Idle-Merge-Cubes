package com.lewydo.idlemergecubes.game.actors.panel.goals.body

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.goals.util.AGoalsProgress
import com.lewydo.idlemergecubes.game.actors.panel.grid.ACube
import com.lewydo.idlemergecubes.game.systems.goals.GoalObjective
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter

// ═════════════════════════════════════════════════════════════════════════════
//  AReachLevelBody — контент для GoalObjective.ReachLevel
//
//   [Cube N]  Reach this cube level            5/6
//             [===============------------]
//
//  Перевикористовується: ASimpleGoalPanel + ATimedGoalPanel.
//  Дамб-view: лише setObjective() / setProgress(). Жодної логіки/моделі.
// ═════════════════════════════════════════════════════════════════════════════

class AReachLevelBody(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Sizes
    // ------------------------------------------------------------------------
    private val CUBE_SIZE = 129f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterCube  = FontParameter().setCharacters(FontParameter.CharType.NUMBERS).setSize(85).setBorder(1f, GameColor.brown_8D3800).setShadow(3, 3, GameColor.purple_350080)
    private val parameterDesc  = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(56)
    private val parameterCount = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "/").setSize(56)

    private val lsCube  = FontFactory.create(screen, parameterCube, screen.fontGenerator_Nunito_Black, Color.WHITE)
    private val lsDesc  = FontFactory.create(screen, parameterDesc, screen.fontGenerator_Nunito_Medium, Color.WHITE)
    private val lsCount = FontFactory.create(screen, parameterCount, screen.fontGenerator_Nunito_Medium, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCube           = ACube(screen, 0, 0, lsCube)
    private val aDescLbl        = Label("Reach this cube level", lsDesc)
    private val aCountLbl       = Label("0", lsCount)
    private val aCountTargetLbl = Label("/5", lsCount).apply { color.a = 0.5f }
    private val aProgress       = AGoalsProgress(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCube()
        addDescLbl()
        addProgress()
        addCountLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCube() {
        aCube.setSize(CUBE_SIZE, CUBE_SIZE)
        add(aCube) { startToStart(margin = 56f); centerY() }
    }

    private fun addDescLbl() {
        aDescLbl.setSize(534f, 76f)
        add(aDescLbl) { startToEnd(aCube, margin = 28f); topToTop(aCube) }
    }

    private fun addProgress() {
        aProgress.setSize(1574f, 35f)
        add(aProgress) { startToStart(aDescLbl); bottomToBottom(aCube) }
    }

    private fun addCountLbl() {
        aCountTargetLbl.setSize(50f, 76f)
        add(aCountTargetLbl) { endToEnd(aProgress); topToTop(aDescLbl) }
        aCountTargetLbl.setAlignment(Align.right)

        aCountLbl.setSize(34f, 76f)
        add(aCountLbl) { endToStart(aCountTargetLbl, 3f); topToTop(aCountTargetLbl) }
        aCountLbl.setAlignment(Align.right)
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    fun setDescription(text: String) {
        aDescLbl.setText(text)
    }

    fun setObjective(objective: GoalObjective.ReachLevel) {
        aCube.setLevel(objective.targetLevel)
        aCountTargetLbl.setText("/${objective.targetLevel}")
        aCountLbl.setText("0")

        aCountTargetLbl.pack()
        aCountLbl.pack()

        aProgress.updateProgress(0f)
    }

    fun setProgress(progress: GoalProgress.ReachLevel) {
        aCountTargetLbl.setText("/${progress.target}")
        aCountLbl.setText("${progress.current}")

        aCountTargetLbl.pack()
        aCountLbl.pack()

        aProgress.updateProgress(progress.progress * 100f)
    }

}