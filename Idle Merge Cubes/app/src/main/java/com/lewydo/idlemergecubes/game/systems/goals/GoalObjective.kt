package com.lewydo.idlemergecubes.game.systems.goals

// ═════════════════════════════════════════════════════════════════════════════
//  GoalObjective — ЩО гравець має зробити (без таймера, без нагороди)
//
//  Це "форма" задачі. Timed/нагорода/тривалість — окремі обгортки (див. Goal).
//  Додати новий вид задачі = додати сюди підклас + GoalProgress + body + 1 гілку
//  у GoalGenerator/маппінгу. Решта системи не змінюється.
//
//   ReachLevel → досягти куба рівня N            (одне число)
//   Collect    → зібрати набір кубів на дошці     (список вимог)
// ═════════════════════════════════════════════════════════════════════════════

sealed class GoalObjective {

    val typeName: String get() = this::class.simpleName!!

    // ── ReachLevel ──────────────────────────────────────────────────────────
    data class ReachLevel(
        val targetLevel: Int,
    ) : GoalObjective()

    // ── Collect ─────────────────────────────────────────────────────────────
    data class Collect(
        val requirements: List<Requirement>,
    ) : GoalObjective() {
        data class Requirement(val level: Int, val count: Int)
    }
}