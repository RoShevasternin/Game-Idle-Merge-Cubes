package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.utils.OFFLINE_EFFICIENCY
import com.lewydo.idlemergecubes.game.utils.OFFLINE_MAX_SEC
import com.lewydo.idlemergecubes.game.utils.OFFLINE_MIN_SEC
import kotlin.math.min

class OfflineRewardModel(
    private val playerModel: PlayerModel,
) {

    companion object {
        private const val MIN_OFFLINE_SEC = OFFLINE_MIN_SEC
        private const val MAX_OFFLINE_SEC = OFFLINE_MAX_SEC
    }

    // =====================================================
    // CALCULATE
    // =====================================================

    fun calculate(): OfflineResult {
        val now       = System.currentTimeMillis()
        val lastLogin = playerModel.lastLoginTime

        if (lastLogin == 0L) return OfflineResult.None

        val elapsedSec = (now - lastLogin) / 1000f
        if (elapsedSec < MIN_OFFLINE_SEC) return OfflineResult.None

        val clampedSec   = min(elapsedSec, MAX_OFFLINE_SEC)
        val hours        = clampedSec / 3600f
        val level        = playerModel.currentLevel
        val buyPrice     = playerModel.currentBuyPrice
        val cubesPerHour = 3f + level * 0.3f

        // OFFLINE_EFFICIENCY обрізає щоб 8h ≈ стара 3h нагорода за максимум
        val rawReward  = (hours * cubesPerHour * buyPrice * OFFLINE_EFFICIENCY).toLong()
        val minReward  = buyPrice * 5
        val finalReward = maxOf(rawReward, minReward)

        return OfflineResult.Reward(
            coins    = finalReward,
            duration = calcDuration(clampedSec),
        )
    }

    // =====================================================
    // COLLECT
    // =====================================================

    fun collect(result: OfflineResult.Reward) {
        playerModel.addCoins(result.coins)
        saveLoginTime()
    }

    fun collectX2(result: OfflineResult.Reward) {
        playerModel.addCoins(result.coins * 2)
        saveLoginTime()
    }

    // =====================================================
    // SAVE LOGIN TIME — викликати при старті та паузі
    // =====================================================

    fun saveLoginTime() {
        // ТЕСТ — закоментуй після перевірки
        //val fiveHoursAgo = System.currentTimeMillis() - (30 * 1000L * 1000L)
        //playerModel.updateLastLoginTime(fiveHoursAgo)

        // РЕАЛЬНЕ — розкоментуй після тесту
        playerModel.updateLastLoginTime(System.currentTimeMillis())
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private fun calcDuration(elapsedSec: Float): OfflineDuration {
        val totalMin = (elapsedSec / 60).toInt()
        val hours    = totalMin / 60
        val minutes  = totalMin % 60
        val seconds  = (elapsedSec % 60).toInt()

        return OfflineDuration(hours, minutes, seconds)
    }

    // =====================================================
    // RESULT
    // =====================================================

    sealed class OfflineResult {

        // Немає нагороди — не показуємо діалог
        object None : OfflineResult()

        // Є нагорода — показуємо діалог
        data class Reward(
            val coins   : Long,
            val duration: OfflineDuration,
        ) : OfflineResult()
    }

    // =====================================================
    // DURATION
    // =====================================================

    data class OfflineDuration(
        val hours  : Int,
        val minutes: Int,
        val seconds: Int,
    ) {
        // "3 HOURS" / "45 MINUTES" / "30 SECONDS" / "2 HOURS 15 MINUTES"
        fun toDisplayString(): String {
            val h = hours.toTimeString("HOUR", "HOURS")
            val m = minutes.toTimeString("MINUTE", "MINUTES")
            val s = seconds.toTimeString("SECOND", "SECONDS")

            return when {
                hours > 0 && minutes > 0 -> "$h $m"
                hours > 0                -> h
                minutes > 0              -> m
                else                     -> s
            }
        }

        private fun Int.toTimeString(singular: String, plural: String): String {
            return "$this ${if (this == 1) singular else plural}"
        }
    }
}