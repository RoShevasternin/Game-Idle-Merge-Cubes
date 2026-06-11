package com.lewydo.idlemergecubes.game.systems.goals

// ═════════════════════════════════════════════════════════════════════════════
//  GoalContext — все що потрібно генератору, передаємо одним об'єктом
// ═════════════════════════════════════════════════════════════════════════════

data class GoalContext(
    val maxCube    : Int,   // поточний макс куб на grid
    val buyLevel   : Int,   // рівень кнопки BUY
    val playerLevel: Int,   // рівень гравця
)