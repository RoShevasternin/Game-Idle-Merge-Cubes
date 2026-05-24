package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.state.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.ln
import kotlin.math.pow

class PlayerModel(
    private val state: GameState,
    private val scope: CoroutineScope
) {

    companion object {
        private const val BASE_XP      = 100.0
        private const val GROWTH       = 1.33
        private const val GROWTH_DELTA = GROWTH - 1.0
    }

    // ------------------------------------------------------------------------
    // Flows
    // ------------------------------------------------------------------------

    val coinsFlow = state.coinsFlow
    val xpFlow    = state.xpFlow

    val levelFlow: StateFlow<Int> = state.xpFlow
        .map { xpToLevel(it) }
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.Eagerly, xpToLevel(state.xpFlow.value))

    val buyPriceFlow: Flow<Long> = levelFlow
        .map { (8 + it * 2).toLong() }

    // ------------------------------------------------------------------------
    // Current values
    // ------------------------------------------------------------------------

    val currentCoins   : Long    get() = state.coinsFlow.value
    val currentXp      : Long    get() = state.xpFlow.value
    val currentLevel   : Int     get() = levelFlow.value
    val currentBuyPrice: Long    get() = (8 + currentLevel * 2).toLong()
    val lastLoginTime  : Long    get() = state.lastLoginTime
    val adsRemoved     : Boolean get() = state.adsRemoved
    val tutorialStep   : Int     get() = state.tutorialStep

    // ------------------------------------------------------------------------
    // Coins
    // ------------------------------------------------------------------------

    fun addCoins(amount: Long) {
        if (amount <= 0) return
        state.coinsFlow.value += amount
    }

    fun spendCoins(amount: Long): Boolean {
        if (amount !in 1..currentCoins) return false
        state.coinsFlow.value -= amount
        return true
    }

    // ------------------------------------------------------------------------
    // XP + Level
    // ------------------------------------------------------------------------

    fun addXp(amount: Long) {
        if (amount <= 0) return
        state.xpFlow.value += amount
        // levelFlow оновиться автоматично через map
    }

    private fun xpToLevel(xp: Long): Int {
        if (xp <= 0) return 1
        return (1 + ln(1 + xp * GROWTH_DELTA / BASE_XP) / ln(GROWTH))
            .toInt().coerceAtLeast(1)
    }

    // ------------------------------------------------------------------------
    // Level progress
    // ------------------------------------------------------------------------

    fun xpForLevel(level: Int = currentLevel): Long =
        (BASE_XP * GROWTH.pow(level - 1)).toLong()

    fun xpToReachLevel(level: Int): Long =
        (BASE_XP * (GROWTH.pow(level - 1) - 1.0) / GROWTH_DELTA).toLong()

    fun xpInCurrentLevel(): Long =
        currentXp - xpToReachLevel(currentLevel)

    fun levelProgress(): Float {
        val needed = xpForLevel().toFloat()
        return if (needed <= 0f) 0f
        else (xpInCurrentLevel().toFloat() / needed).coerceIn(0f, 1f)
    }

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------

    fun setAdsRemoved()               { state.adsRemoved    = true }
    fun updateLastLoginTime(ts: Long) { state.lastLoginTime = ts   }
    fun updateTutorialStep(step: Int) { state.tutorialStep  = step }
}