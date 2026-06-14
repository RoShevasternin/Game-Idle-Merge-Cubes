package com.lewydo.idlemergecubes.game.systems.goals

import kotlin.math.pow
import kotlin.random.Random

// ═════════════════════════════════════════════════════════════════════════════
//  GoalGenerator — генерує задачі залежно від контексту гравця
//
//  Дві осі рішення (незалежні):
//    1) ЯКИЙ objective:  ReachLevel | Collect
//    2) ЧИ timed:        ~33% задач отримують таймер
//
//  Завдяки декомпозиції timed може бути будь-якої форми:
//    "досягти рівня на час"  (ReachLevel + timer)
//    "зібрати куби на час"   (Collect + timer)
//
//  Правила:
//   • Не повторювати ту саму КАТЕГОРІЮ підряд (lastGoal.category)
//   • ReachLevel → ціль = maxCube + 1
//   • Collect    → 2–3 вимоги, рівні від buyLevel до maxCube
//   • Всі рівні ≥ buyLevel → гравець завжди може виконати
//
//  Reward:
//   base  = buyPrice × (1 + maxCube × 0.12) × (1 + playerLevel × 0.07)
//   ReachLevel × 2.0 | Collect × 3.5 | (timed ще × 1.6)
//
//  Time estimation (timed):
//   Для куба рівня L при buyLevel B:
//     buys   = 2^(L-B),  merges = buys-1,  perCube = buys×2с + merges×3с
//   Округлення до кратного 5, [30..120]
// ═════════════════════════════════════════════════════════════════════════════

object GoalGenerator {

    private const val TIMED_CHANCE  = 0.33f
    private const val TIMED_REWARD_MULT = 1.6

    // ── Entry point ───────────────────────────────────────────────────────────

    fun generate(lastGoal: Goal?, ctx: GoalContext): Goal {
        val objective = pickObjective(lastGoal, ctx)
        val timed     = decideTimed(lastGoal, objective)

        val timeLimit = if (timed) estimateTime(objective, ctx.buyLevel) else null
        val reward    = calcReward(objective, timed, ctx)

        return Goal(objective = objective, reward = reward, timeLimitSec = timeLimit)
    }

    // ── Objective selection ─────────────────────────────────────────────────
    //
    // Уникаємо дубля форми objective підряд (ReachLevel/Collect),
    // інакше 50/50.

    private fun pickObjective(lastGoal: Goal?, ctx: GoalContext): GoalObjective {
        val lastWasCollect = lastGoal?.objective is GoalObjective.Collect
        val lastWasReach   = lastGoal?.objective is GoalObjective.ReachLevel

        return when {
            lastWasCollect -> buildReachLevel(ctx)
            lastWasReach   -> buildCollect(ctx)
            else           -> if (Random.nextBoolean()) buildReachLevel(ctx) else buildCollect(ctx)
        }
    }

    private fun decideTimed(lastGoal: Goal?, objective: GoalObjective): Boolean {
        if (lastGoal?.isTimed == true) return false           // не два timed підряд
        return Random.nextFloat() < TIMED_CHANCE
    }

    // ── ReachLevel ────────────────────────────────────────────────────────────

    private fun buildReachLevel(ctx: GoalContext): GoalObjective.ReachLevel {
        val target = (ctx.maxCube + 1).coerceAtLeast(ctx.buyLevel + 1)
        return GoalObjective.ReachLevel(targetLevel = target)
    }

    // ── Collect ─────────────────────────────────────────────────────────────

    private fun buildCollect(ctx: GoalContext): GoalObjective.Collect {
        val reqs = buildRequirements(ctx, count = Random.nextInt(2, 4))
        return GoalObjective.Collect(requirements = reqs)
    }

    // ── Requirements builder ──────────────────────────────────────────────────
    //
    // Рівні від buyLevel до maxCube — гарантовано досяжні.
    // Чим вищий рівень відносно buy → тим менше кубів потрібно.

    private fun buildRequirements(ctx: GoalContext, count: Int): List<GoalObjective.Collect.Requirement> {
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
            GoalObjective.Collect.Requirement(level, maxCount)
        }
    }

    // ── Time estimation ───────────────────────────────────────────────────────

    private fun estimateTime(objective: GoalObjective, buyLevel: Int): Int {
        val rawSec = when (objective) {
            is GoalObjective.ReachLevel -> {
                val diff   = (objective.targetLevel - buyLevel).coerceAtLeast(0)
                val buys   = 2.0.pow(diff).toInt()
                val merges = (buys - 1).coerceAtLeast(0)
                buys * 2 + merges * 3
            }
            is GoalObjective.Collect -> objective.requirements.sumOf { req ->
                val diff   = (req.level - buyLevel).coerceAtLeast(0)
                val buys   = 2.0.pow(diff).toInt()
                val merges = (buys - 1).coerceAtLeast(0)
                (buys * 2 + merges * 3) * req.count
            }
        }
        val rounded = ((rawSec + 2) / 5) * 5
        return rounded.coerceIn(30, 120)
    }

    // ── Reward ────────────────────────────────────────────────────────────────

    private fun calcReward(objective: GoalObjective, timed: Boolean, ctx: GoalContext): Long {
        val shapeCoeff = when (objective) {
            is GoalObjective.ReachLevel -> 2.0
            is GoalObjective.Collect    -> 3.5
        }
        val timedCoeff = if (timed) TIMED_REWARD_MULT else 1.0
        val buyPrice   = (8 + ctx.playerLevel * 2).toDouble()
        val cubeBonus  = 1.0 + ctx.maxCube     * 0.12
        val lvlBonus   = 1.0 + ctx.playerLevel * 0.07
        return (buyPrice * cubeBonus * lvlBonus * shapeCoeff * timedCoeff).toLong().coerceAtLeast(10L)
    }
}