package com.lewydo.idlemergecubes.game.systems.goals

// ═════════════════════════════════════════════════════════════════════════════
//  Goal — повна задача: objective + опціональний таймер + нагорода
//
//  timeLimitSec == null  → звичайна задача
//  timeLimitSec != null  → та сама задача, але на час (timed-модифікатор)
//
//  Тобто "timed" — НЕ окремий тип, а прапорець над будь-яким objective:
//    ReachLevel на час  | Collect на час  | (майбутні) ... на час
//
//  category — для аналітики/UI: "simple" | "combined" | "timed"
//  (timed має пріоритет над формою objective)
// ═════════════════════════════════════════════════════════════════════════════
data class Goal(
    val objective   : GoalObjective,
    val reward      : Long,
    val timeLimitSec: Int? = null,
) {
    val isTimed: Boolean get() = timeLimitSec != null

    // Категорія для аналітики та вибору бейджа
    val category: Category get() = when {
        isTimed                            -> Category.TIMED
        objective is GoalObjective.Collect -> Category.COMBINED
        else                               -> Category.SIMPLE
    }

    enum class Category { SIMPLE, COMBINED, TIMED }
}