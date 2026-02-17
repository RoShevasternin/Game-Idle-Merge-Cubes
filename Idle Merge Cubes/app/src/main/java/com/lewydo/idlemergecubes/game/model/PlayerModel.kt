package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.data.PlayerData
import com.lewydo.idlemergecubes.game.dataStore.DS_Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlin.math.ln
import kotlin.math.pow

class PlayerModel(
    private val ds: DS_Player,
    scope: CoroutineScope
) {

    companion object {
        private const val BASE_XP = 100.0
        private const val GROWTH = 1.33
        private const val GROWTH_DELTA = GROWTH - 1.0
    }

    // =====================================================
    // Джерело правди
    // =====================================================

    val playerFlow: StateFlow<PlayerData> =
        ds.flow.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = ds.flow.value
        )

    val currentPlayer: PlayerData
        get() = playerFlow.value

    // =====================================================
    // XP
    // =====================================================

    val xpFlow: StateFlow<Long> =
        playerFlow
            .map { it.xp }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, currentPlayer.xp)

    val currentXp: Long
        get() = xpFlow.value

    fun addXp(amount: Long) {
        if (amount <= 0) return

        ds.update { data ->
            data.copy(xp = data.xp + amount)
        }
    }

    fun addXpFromMerge(cubeLevel: Int) {
        val xpGain = (cubeLevel * 2f).toLong()
        addXp(xpGain)
    }

    // =====================================================
    // LEVEL (обчислюється з XP)
    // =====================================================

    val levelFlow: StateFlow<Int> =
        xpFlow
            .map { calculateLevelFromXp(it) }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, calculateLevelFromXp(currentXp))

    val currentLevel: Int
        get() = levelFlow.value

    private fun calculateLevelFromXp(xp: Long): Int {
        if (xp <= 0) return 1

        val value = 1 + (xp * GROWTH_DELTA / BASE_XP)
        val level = 1 + ln(value) / ln(GROWTH)

        return level.toInt().coerceAtLeast(1)
    }

    // =====================================================
    // Прогрес рівня
    // =====================================================

    fun xpToNextLevel(level: Int = currentLevel): Long {
        return (BASE_XP * GROWTH.pow(level - 1)).toLong()
    }

    fun xpToReachLevel(level: Int): Long {
        return (BASE_XP * (GROWTH.pow(level - 1) - 1.0) / GROWTH_DELTA).toLong()
    }

    fun xpIntoCurrentLevel(): Long {
        val levelStartXp = xpToReachLevel(currentLevel)
        return currentXp - levelStartXp
    }

    fun progressPercent(): Float {
        val progress = xpIntoCurrentLevel().toFloat()
        val needed = xpToNextLevel().toFloat()

        if (needed <= 0f) return 0f

        return (progress / needed).coerceIn(0f, 1f)
    }

    fun progressPercent100(): Int {
        return (progressPercent() * 100f).toInt()
    }

    // =====================================================
    // COINS
    // =====================================================

    val coinsFlow: StateFlow<Long> =
        playerFlow
            .map { it.coins }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, currentPlayer.coins)

    val currentCoins: Long
        get() = coinsFlow.value

    fun addCoins(amount: Long) {
        if (amount <= 0) return

        ds.update { data ->
            data.copy(coins = data.coins + amount)
        }
    }

    fun spendCoins(amount: Long): Boolean {
        if (amount <= 0) return false
        if (currentCoins < amount) return false

        ds.update { data ->
            data.copy(coins = data.coins - amount)
        }

        return true
    }

    // =====================================================
    // Ads / IAP
    // =====================================================

    val adsRemovedFlow: StateFlow<Boolean> =
        playerFlow
            .map { it.adsRemoved }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, currentPlayer.adsRemoved)

    fun setAdsRemoved() {
        ds.update { data ->
            data.copy(adsRemoved = true)
        }
    }

    // =====================================================
    // Offline
    // =====================================================

    fun updateLastLoginTime(timestamp: Long) {
        ds.update { data ->
            data.copy(lastLoginTime = timestamp)
        }
    }

    val lastLoginTime: Long
        get() = currentPlayer.lastLoginTime
}
