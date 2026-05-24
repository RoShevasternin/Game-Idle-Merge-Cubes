package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.state.GameState
import com.lewydo.idlemergecubes.game.utils.gdxGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

class MergeBonusModel(
    private val state      : GameState,
    private val gridModel  : GridModel,
    private val playerModel: PlayerModel,
    scope: CoroutineScope
) {

    // ------------------------------------------------------------------------
    // Flows
    // ------------------------------------------------------------------------

    val countFlow = state.mergeBonusCountFlow
    val goalFlow  = state.mergeBonusGoalFlow

    val progressFlow: StateFlow<Float> = combine(countFlow, goalFlow) { count, goal ->
        (count.toFloat() / goal).coerceIn(0f, 1f)
    }.stateIn(scope, SharingStarted.Eagerly, currentProgress)

    val rewardFlow: StateFlow<Int> = combine(
        gridModel.gridFlow,
        playerModel.levelFlow
    ) { _, _ -> calculateReward() }
        .stateIn(scope, SharingStarted.Eagerly, calculateReward())

    // ------------------------------------------------------------------------
    // Current values
    // ------------------------------------------------------------------------

    val currentProgress: Float
        get() = (state.mergeBonusCountFlow.value.toFloat() / state.mergeBonusGoalFlow.value)
            .coerceIn(0f, 1f)

    val isReady: Boolean
        get() = state.mergeBonusCountFlow.value >= state.mergeBonusGoalFlow.value

    val currentReward: Int
        get() = calculateReward()

    // ------------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------------

    fun onMerge(isEnchanted: Boolean = false) {
        if (isReady) return
        state.mergeBonusCountFlow.value =
            if (isEnchanted) state.mergeBonusGoalFlow.value
            else state.mergeBonusCountFlow.value + 1
    }

    fun collect() {
        val reward = currentReward
        if (reward <= 0) return
        gdxGame.analytics.collectIdle(reward.toLong())
        playerModel.addCoins(reward.toLong())
        reset()
    }

    fun collectX2() {
        val reward = currentReward
        if (reward <= 0) return
        gdxGame.analytics.collectIdleX2((reward * 2).toLong())
        playerModel.addCoins((reward * 2).toLong())
        reset()
    }

    // ------------------------------------------------------------------------
    // Private
    // ------------------------------------------------------------------------

    private fun reset() {
        state.mergeBonusCountFlow.value = 0
        state.mergeBonusGoalFlow.value  = Random.nextInt(10, 21)
    }

    private fun calculateReward(): Int =
        (gridModel.totalPower() * 2) + (playerModel.currentLevel * 4)
}