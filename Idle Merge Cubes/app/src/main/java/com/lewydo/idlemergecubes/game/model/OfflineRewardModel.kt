package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.utils.OFFLINE_EFFICIENCY
import com.lewydo.idlemergecubes.game.utils.OFFLINE_MAX_SEC
import com.lewydo.idlemergecubes.game.utils.OFFLINE_MIN_SEC
import kotlin.math.min

class OfflineRewardModel(private val playerModel: PlayerModel) {

    // ------------------------------------------------------------------------
    // Calculate
    // ------------------------------------------------------------------------

    fun calculate(): OfflineResult {
        val now        = System.currentTimeMillis()
        val lastLogin  = playerModel.lastLoginTime
        if (lastLogin == 0L) return OfflineResult.None

        val elapsedSec = (now - lastLogin) / 1000f
        if (elapsedSec < OFFLINE_MIN_SEC) return OfflineResult.None

        val clampedSec = min(elapsedSec, OFFLINE_MAX_SEC)

        return OfflineResult.Reward(
            coins    = calculateReward(clampedSec),
            duration = toDuration(clampedSec),
        )
    }

    private fun calculateReward(elapsedSec: Float): Long {
        val hours        = elapsedSec / 3600f
        val level        = playerModel.currentLevel
        val buyPrice     = playerModel.currentBuyPrice
        val cubesPerHour = 3f + level * 0.3f
        val rawReward    = (hours * cubesPerHour * buyPrice * OFFLINE_EFFICIENCY).toLong()
        val minReward    = buyPrice * 5
        return maxOf(rawReward, minReward)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    fun collect(result: OfflineResult.Reward) {
        playerModel.addCoins(result.coins)
        saveLoginTime()
    }

    fun collectX2(result: OfflineResult.Reward) {
        playerModel.addCoins(result.coins * 2)
        saveLoginTime()
    }

    // ------------------------------------------------------------------------
    // Login time
    // ------------------------------------------------------------------------

    fun saveLoginTime() {
        playerModel.updateLastLoginTime(System.currentTimeMillis())
    }

    // ------------------------------------------------------------------------
    // Private
    // ------------------------------------------------------------------------

    private fun toDuration(elapsedSec: Float): OfflineDuration {
        val totalMin = (elapsedSec / 60).toInt()
        return OfflineDuration(
            hours   = totalMin / 60,
            minutes = totalMin % 60,
            seconds = (elapsedSec % 60).toInt(),
        )
    }

    // ------------------------------------------------------------------------
    // Result
    // ------------------------------------------------------------------------

    sealed class OfflineResult {
        data object None : OfflineResult()
        data class Reward(val coins: Long, val duration: OfflineDuration) : OfflineResult()
    }

    // ------------------------------------------------------------------------
    // Duration
    // ------------------------------------------------------------------------

    data class OfflineDuration(val hours: Int, val minutes: Int, val seconds: Int) {

        fun toDisplayString(): String {
            val h = hours.toLabel("HOUR", "HOURS")
            val m = minutes.toLabel("MINUTE", "MINUTES")
            val s = seconds.toLabel("SECOND", "SECONDS")
            return when {
                hours > 0 && minutes > 0 -> "$h $m"
                hours > 0                -> h
                minutes > 0              -> m
                else                     -> s
            }
        }

        private fun Int.toLabel(singular: String, plural: String) =
            "$this ${if (this == 1) singular else plural}"
    }
}