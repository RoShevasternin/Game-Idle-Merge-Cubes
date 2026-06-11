package com.lewydo.idlemergecubes.game.manager

import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.services.tiktok.TikTokManager

class TutorialManager(private val playerModel: PlayerModel) {

    // ------------------------------------------------------------------------
    // Step
    // ------------------------------------------------------------------------

    enum class Step { BUY, MERGE, DONE }

    // ← завжди читає актуальний GameState, не кешує при старті
    val currentStep: Step
        get() = Step.entries[playerModel.tutorialStep.coerceIn(0, Step.entries.lastIndex)]

    val isDone: Boolean get() = currentStep == Step.DONE

    // ------------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------------

    fun onBuyDone() {
        if (currentStep != Step.BUY) return
        gdxGame.analytics.tutorialBegin()
        saveStep(Step.MERGE)
    }

    fun onMergeDone() {
        if (currentStep != Step.MERGE) return
        gdxGame.analytics.tutorialComplete()
        saveStep(Step.DONE)
    }

    fun onCubePositionChanged() {
        if (currentStep != Step.MERGE) return
        GlobalEvents.emit(GlobalEvents.EventType.TUTORIAL_CUBE_POSITION_CHANGED)
    }

    // ------------------------------------------------------------------------
    // Private
    // ------------------------------------------------------------------------

    private fun saveStep(step: Step) {
        playerModel.updateTutorialStep(step.ordinal)
        GlobalEvents.emit(GlobalEvents.EventType.TUTORIAL_STEP_CHANGED)
    }
}