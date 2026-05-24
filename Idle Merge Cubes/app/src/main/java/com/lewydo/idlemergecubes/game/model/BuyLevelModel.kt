package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.state.GameState
import com.lewydo.idlemergecubes.game.utils.maxOrZero
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BuyLevelModel(
    private val state: GameState,
    scope: CoroutineScope
) {

    companion object {
        fun calculateBuyLevel(maxCube: Int): Int =
            if (maxCube < 5) 1 else maxOf(1, (maxCube - 1) / 2)

        fun nextTierTarget(buyLevel: Int): Int = 2 * buyLevel + 3

        fun tierProgress(maxCube: Int, buyLevel: Int): Float {
            val base   = if (buyLevel == 1) 1 else 2 * buyLevel + 1
            val target = nextTierTarget(buyLevel)
            if (target <= base) return 1f
            return ((maxCube - base).toFloat() / (target - base)).coerceIn(0f, 1f)
        }

        // Поточний прогрес в кубах (current / total) для відображення "1/2"
        fun tierCubeProgress(maxCube: Int, buyLevel: Int): Pair<Int, Int> {
            val base    = if (buyLevel == 1) 1 else 2 * buyLevel + 1
            val target  = nextTierTarget(buyLevel)
            val total   = target - base                          // скільки всього треба пройти
            val current = (maxCube - base).coerceAtLeast(0)     // скільки вже пройдено
            return current to total
        }
    }

    // ------------------------------------------------------------------------
    // Flows — автоматично рахуються з gridFlow як levelFlow з xpFlow
    // ------------------------------------------------------------------------

    val buyLevelFlow: StateFlow<Int> = state.gridFlow
        .map { grid -> calculateBuyLevel(grid.maxOrZero()) }
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.Eagerly, calculateBuyLevel(state.gridFlow.value.maxOrZero()))

    val nextTargetFlow: StateFlow<Int> = buyLevelFlow
        .map { nextTierTarget(it) }
        .stateIn(scope, SharingStarted.Eagerly, nextTierTarget(buyLevelFlow.value))

    val progressFlow: StateFlow<Float> = state.gridFlow
        .map { grid ->
            val maxCube  = grid.maxOrZero()
            val buyLevel = calculateBuyLevel(maxCube)
            tierProgress(maxCube, buyLevel)
        }
        .stateIn(scope, SharingStarted.Eagerly, 0f)

    val currentBuyLevel: Int get() = buyLevelFlow.value

    // ------------------------------------------------------------------------
    // Upgrade detection — відстежуємо зміну для auto-upgrade кубів
    // ------------------------------------------------------------------------

    private var lastKnownBuyLevel: Int? = null  // ← null до першого виклику

    fun checkAndUpgrade(): UpgradeResult? {
        val grid     = state.gridFlow.value
        val maxCube  = grid.maxOrZero()
        val newLevel = calculateBuyLevel(maxCube)

        // Перший виклик після завантаження — просто синхронізуємо без апгрейду
        if (lastKnownBuyLevel == null) {
            lastKnownBuyLevel = newLevel
            return null
        }

        val oldLevel = lastKnownBuyLevel!!
        lastKnownBuyLevel = newLevel

        if (newLevel <= oldLevel) return null

        val upgradedIndices = grid.indices.filter { i -> grid[i] in 1 until newLevel }
        if (upgradedIndices.isNotEmpty()) {
            val upgraded = grid.toMutableList()
            upgradedIndices.forEach { upgraded[it] = newLevel }
            state.gridFlow.value = upgraded
        }

        return UpgradeResult(newLevel, upgradedIndices)
    }

    // ------------------------------------------------------------------------
    // Result
    // ------------------------------------------------------------------------

    data class UpgradeResult(
        val newLevel       : Int,
        val upgradedIndices: List<Int>,
    )

}