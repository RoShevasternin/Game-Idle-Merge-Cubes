package com.lewydo.idlemergecubes.game.manager

import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.tiktok.TikTokManager
import kotlinx.coroutines.CoroutineScope

class TutorialManager(val coroutine: CoroutineScope) {

    enum class Step { BUY, MERGE, DONE }

    private var localStep: Step? = null

    val currentStep: Step
        get() {
            if (localStep == null) {
                localStep = Step.entries[
                    gdxGame.ds_Player.flow.value.tutorialStep.coerceIn(0, Step.entries.lastIndex)
                ]
            }
            return localStep!!
        }

    val isDone: Boolean get() = currentStep == Step.DONE

    fun onBuyDone() {
        if (localStep != Step.BUY) return
        gdxGame.analytics.tutorialBegin()
        saveStep(Step.MERGE)
    }

    fun onMergeDone() {
        if (localStep != Step.MERGE) return
        gdxGame.analytics.tutorialComplete()
        saveStep(Step.DONE)
        TikTokManager.tutorialComplete()
    }

    fun onCubePositionChanged() {
        if (localStep != Step.MERGE) return
        GlobalEvents.emit(GlobalEvents.EventType.TUTORIAL_CUBE_POSITION_CHANGED)
    }

    private fun saveStep(step: Step) {
        localStep = step                          // ← синхронно одразу
        gdxGame.ds_Player.update { it.copy(tutorialStep = step.ordinal) }  // ← persist async
        GlobalEvents.emit(GlobalEvents.EventType.TUTORIAL_STEP_CHANGED)
    }
}