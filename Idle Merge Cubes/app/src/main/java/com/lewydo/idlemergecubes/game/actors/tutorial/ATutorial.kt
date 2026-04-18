package com.lewydo.idlemergecubes.game.actors.tutorial

import com.lewydo.idlemergecubes.game.manager.TutorialManager
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ATutorial(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aHand = ATutorialHand(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        disable() // не блокуємо дотики
        addHand()

        registerEvents()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHand() {
        addActor(aHand)
        aHand.setSize(331f, 479f)
    }


    // ------------------------------------------------------------------------
    // Public
    // ------------------------------------------------------------------------

    fun start() {
        updateStep()
    }

    fun onResume() {
        if (!gdxGame.tutorialManager.isDone) updateStep()
    }

    // ------------------------------------------------------------------------
    // Private
    // ------------------------------------------------------------------------

    private fun updateStep() {
        when (gdxGame.tutorialManager.currentStep) {
            TutorialManager.Step.BUY   -> showBuyHint()
            TutorialManager.Step.MERGE -> showMergeHint()
            TutorialManager.Step.DONE  -> aHand.hide()
        }
    }

    private fun showBuyHint() {
        val pos = GlobalStagePositions.get(GlobalStagePositions.Position.BUY_BTN)
        aHand.showTap(pos.x, pos.y)
    }

    private fun showMergeHint() {
        val from = GlobalStagePositions.get(GlobalStagePositions.Position.CUBE_0)
        val to   = GlobalStagePositions.get(GlobalStagePositions.Position.CUBE_1)

        // захист — якщо позиції ще не зареєстровані (обидва Vector2 = 0,0)
        if (from.isZero && to.isZero) return

        aHand.showDrag(from, to)
    }

    private fun registerEvents() {
        coroutine?.launch {
            GlobalEvents.events.collect { event -> runGDX { when (event) {
                GlobalEvents.EventType.TUTORIAL_STEP_CHANGED          -> updateStep()
                GlobalEvents.EventType.TUTORIAL_CUBE_POSITION_CHANGED -> showMergeHint()
                else -> {}
            } } }
        }
    }

}