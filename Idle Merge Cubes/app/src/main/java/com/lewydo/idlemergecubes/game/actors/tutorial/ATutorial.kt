package com.lewydo.idlemergecubes.game.actors.tutorial

import com.lewydo.idlemergecubes.game.manager.TutorialManager
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.runGDX
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
        disable()
        addActor(aHand)
        aHand.setSize(331f, 479f)

        collectEvents()
    }

    // ------------------------------------------------------------------------
    // Public
    // ------------------------------------------------------------------------

    fun start()    { updateStep() }
    fun onResume() { if (!gdxGame.tutorialManager.isDone) updateStep() }

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
        val pos = GlobalStagePositions.get(GlobalStagePositions.Key.BUY_BTN)
        aHand.showTap(pos.x, pos.y)
    }

    private fun showMergeHint() {
        val from = GlobalStagePositions.get(GlobalStagePositions.Key.CUBE_0)
        val to   = GlobalStagePositions.get(GlobalStagePositions.Key.CUBE_1)
        if (from.isZero || to.isZero) return
        aHand.showDrag(from, to)
    }

    private fun collectEvents() {
        coroutine?.launch {
            GlobalEvents.events.collect { event ->
                runGDX {
                    when (event) {
                        GlobalEvents.EventType.TUTORIAL_STEP_CHANGED          -> updateStep()
                        GlobalEvents.EventType.TUTORIAL_CUBE_POSITION_CHANGED -> showMergeHint()
                        else -> {}
                    }
                }
            }
        }
    }
}