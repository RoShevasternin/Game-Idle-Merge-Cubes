package com.lewydo.idlemergecubes.game.systems.goals

import kotlin.math.pow
import kotlin.random.Random

// ═════════════════════════════════════════════════════════════════════════════
//  GoalGenerator — генерує задачі залежно від контексту гравця
//
//  Правила:
//   • Не повторювати той самий тип підряд (buildList фільтрує)
//   • Simple   → ціль = maxCube + 1
//   • Combined → 2–3 вимоги, рівні від buyLevel до maxCube
//   • Timed    → 1–2 вимоги + розрахований час
//   • Всі рівні ≥ buyLevel → гравець завжди може виконати
//
//  Reward:
//   base  = buyPrice × (1 + maxCube × 0.12) × (1 + playerLevel × 0.07)
//   Simple × 2.0 | Combined × 3.5 | Timed × 5.5
//
//  Time estimation (Timed):
//   Для куба рівня L при buyLevel B:
//     buys   = 2^(L-B)       → кількість натискань BUY
//     merges = 2^(L-B) - 1   → кількість мерджів
//     perCube = buys × 2с + merges × 3с
//   Округлення до кратного 5, [30..120]
// ═════════════════════════════════════════════════════════════════════════════

object GoalGenerator {

    // ── Entry point ───────────────────────────────────────────────────────────

    fun generate(lastGoal: Goal?, ctx: GoalContext): Goal {
        val generators = buildList {
            if (lastGoal !is Goal.Simple)   add { generateSimple(ctx) }
            if (lastGoal !is Goal.Combined) add { generateCombined(ctx) }
            if (lastGoal !is Goal.Timed)    add { generateTimed(ctx) }
        }
        return generators.random().invoke()
    }

    // ── Simple ────────────────────────────────────────────────────────────────

    private fun generateSimple(ctx: GoalContext): Goal.Simple {
        val target = (ctx.maxCube + 1).coerceAtLeast(ctx.buyLevel + 1)
        val draft  = Goal.Simple(reward = 0L, targetLevel = target)
        return draft.copy(reward = calcReward(draft, ctx))
    }

    // ── Combined ──────────────────────────────────────────────────────────────

    private fun generateCombined(ctx: GoalContext): Goal.Combined {
        val reqs  = buildRequirements(ctx, count = Random.nextInt(2, 4))
        val draft = Goal.Combined(reward = 0L, requirements = reqs)
        return draft.copy(reward = calcReward(draft, ctx))
    }

    // ── Timed ─────────────────────────────────────────────────────────────────

    private fun generateTimed(ctx: GoalContext): Goal.Timed {
        val reqs  = buildRequirements(ctx, count = Random.nextInt(1, 3))
        val limit = estimateTime(reqs, ctx.buyLevel)
        val draft = Goal.Timed(reward = 0L, requirements = reqs, timeLimitSec = limit)
        return draft.copy(reward = calcReward(draft, ctx))
    }

    // ── Requirements builder ──────────────────────────────────────────────────
    //
    // Рівні від buyLevel до maxCube — гарантовано досяжні.
    // Чим вищий рівень відносно buy → тим менше кубів потрібно.

    private fun buildRequirements(ctx: GoalContext, count: Int): List<Goal.Combined.Requirement> {
        val maxTarget    = ctx.maxCube.coerceAtLeast(ctx.buyLevel + 1)
        val levelRange   = (ctx.buyLevel..maxTarget).toList()
        val chosenLevels = levelRange.shuffled().take(count.coerceAtMost(levelRange.size))

        return chosenLevels.sortedDescending().map { level ->
            val diff     = (level - ctx.buyLevel).coerceAtLeast(0)
            val maxCount = when (diff) {
                0    -> Random.nextInt(4, 8)
                1    -> Random.nextInt(2, 5)
                2    -> Random.nextInt(1, 3)
                else -> 1
            }
            Goal.Combined.Requirement(level, maxCount)
        }
    }

    // ── Time estimation ───────────────────────────────────────────────────────

    private fun estimateTime(reqs: List<Goal.Combined.Requirement>, buyLevel: Int): Int {
        val rawSec = reqs.sumOf { req ->
            val diff     = (req.level - buyLevel).coerceAtLeast(0)
            val buys     = 2.0.pow(diff).toInt()
            val merges   = (buys - 1).coerceAtLeast(0)
            val perCube  = buys * 2 + merges * 3
            perCube * req.count
        }
        val rounded = ((rawSec + 2) / 5) * 5
        return rounded.coerceIn(30, 120)
    }

    // ── Reward ────────────────────────────────────────────────────────────────

    private fun calcReward(goal: Goal, ctx: GoalContext): Long {
        val coeff = when (goal) {
            is Goal.Simple   -> 2.0
            is Goal.Combined -> 3.5
            is Goal.Timed    -> 5.5
        }
        val buyPrice  = (8 + ctx.playerLevel * 2).toDouble()
        val cubeBonus = 1.0 + ctx.maxCube     * 0.12
        val lvlBonus  = 1.0 + ctx.playerLevel * 0.07
        return (buyPrice * cubeBonus * lvlBonus * coeff).toLong().coerceAtLeast(10L)
    }
}