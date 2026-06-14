package com.lewydo.idlemergecubes.game.systems.goals

// ═════════════════════════════════════════════════════════════════════════════
//  GoalContext — знімок стану гравця для генерації задачі
//
//   maxCube     — найвищий рівень куба зараз на дошці
//   buyLevel    — рівень куба, який дає кнопка BUY
//   playerLevel — рівень гравця (для масштабу нагороди)
// ═════════════════════════════════════════════════════════════════════════════

data class GoalContext(
    val maxCube    : Int,
    val buyLevel   : Int,
    val playerLevel: Int,
)