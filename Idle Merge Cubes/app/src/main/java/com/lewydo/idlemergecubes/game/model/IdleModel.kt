package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.utils.gdxGame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class IdleModel(
    private val gridModel: GridModel,
    private val playerModel: PlayerModel,
    scope: CoroutineScope
) {

    // =====================================================
    // REWARD FLOW
    // =====================================================

    val rewardFlow: StateFlow<Int> = combine(
        gridModel.gridFlow,
        playerModel.levelFlow
    ) { grid, level ->
        val boardPower = gridModel.totalPower()
        (boardPower * 2) + (level * 4)
    }.stateIn(
        scope,
        SharingStarted.Eagerly,
        calculateReward()
    )

    val currentReward: Int
        get() = rewardFlow.value

    // =====================================================
    // COLLECT
    // =====================================================

    fun collect() {
        val reward = currentReward
        if (reward <= 0) return

        gdxGame.analytics.collectIdle(reward.toLong())
        playerModel.addCoins(reward.toLong())
    }

    // =====================================================
    // COLLECT x2
    // =====================================================

    fun collectX2() {
        val reward = currentReward
        if (reward <= 0) return

        gdxGame.analytics.collectIdleX2((reward * 2).toLong())
        playerModel.addCoins((reward * 2).toLong())
    }

    // =====================================================
    // INITIAL
    // =====================================================

    private fun calculateReward(): Int {
        val boardPower = gridModel.totalPower()
        val level      = playerModel.currentLevel

        return (boardPower * 2) + (level * 4)
    }
}
