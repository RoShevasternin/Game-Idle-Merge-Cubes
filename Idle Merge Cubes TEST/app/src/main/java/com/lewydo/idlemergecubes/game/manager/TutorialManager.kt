package com.lewydo.idlemergecubes.game.manager

import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import kotlinx.coroutines.CoroutineScope

class TutorialManager(val coroutine: CoroutineScope) {

    enum class Step { BUY, MERGE, DONE }

    val currentStep: Step
        get() = Step.entries[gdxGame.ds_Player.flow.value.tutorialStep.coerceIn(0, Step.entries.lastIndex)]

    val isDone: Boolean get() = currentStep == Step.DONE

    fun onBuyDone() {
        if (currentStep != Step.BUY) return
        saveStep(Step.MERGE)
    }

    fun onMergeDone() {
        if (currentStep != Step.MERGE) return
        saveStep(Step.DONE)
    }

    private fun saveStep(step: Step) {
        gdxGame.ds_Player.update { it.copy(tutorialStep = step.ordinal) }
        GlobalEvents.emit(GlobalEvents.EventType.TUTORIAL_STEP_CHANGED)
    }
}