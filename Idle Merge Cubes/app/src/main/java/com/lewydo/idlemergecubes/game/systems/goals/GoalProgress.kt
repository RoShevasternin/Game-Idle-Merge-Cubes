package com.lewydo.idlemergecubes.game.systems.goals

// ═════════════════════════════════════════════════════════════════════════════
//  GoalProgress — поточний прогрес задачі
//  Обчислюється в GoalsModel після кожної зміни grid
// ═════════════════════════════════════════════════════════════════════════════

sealed class GoalProgress {

    // Simple: поточний макс куб vs ціль
    data class Simple(val current: Int, val target: Int) : GoalProgress() {
        val progress: Float   get() = (current.toFloat() / target).coerceIn(0f, 1f)
        val isDone  : Boolean get() = current >= target
    }

    // Combined/Timed: список вимог з прогресом
    data class Combined(val items: List<Item>) : GoalProgress() {

        data class Item(val level: Int, val current: Int, val required: Int) {
            val isDone    : Boolean get() = current >= required
        }

        val isDone: Boolean get() = items.all { it.isDone }
    }
}