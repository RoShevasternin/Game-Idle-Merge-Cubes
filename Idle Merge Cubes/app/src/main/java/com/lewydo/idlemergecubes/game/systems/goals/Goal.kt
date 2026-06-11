package com.lewydo.idlemergecubes.game.systems.goals

import kotlinx.serialization.Serializable

// ═════════════════════════════════════════════════════════════════════════════
//  Goal — sealed class, три типи задач
//
//  typeName  = simpleName — для серіалізації, tied до класу а не до рядків
//  fromState — відновлення після перезапуску без enum
// ═════════════════════════════════════════════════════════════════════════════

sealed class Goal {
    abstract val reward: Long

    val typeName: String get() = this::class.simpleName!!

    // ── Simple ────────────────────────────────────────────────────────────────
    // Досягти рівня куба. Ціль завжди = maxCube + 1 в момент генерації.
    data class Simple(
        override val reward: Long,
        val targetLevel    : Int,
    ) : Goal()

    // ── Combined ──────────────────────────────────────────────────────────────
    // Розмістити кілька кубів різних рівнів одночасно на grid.
    data class Combined(
        override val reward: Long,
        val requirements   : List<Requirement>,
    ) : Goal() {
        @Serializable
        data class Requirement(val level: Int, val count: Int)
    }

    // ── Timed ─────────────────────────────────────────────────────────────────
    // Те саме що Combined, але з таймером [30..120]с кратно 5.
    data class Timed(
        override val reward : Long,
        val requirements    : List<Combined.Requirement>,
        val timeLimitSec    : Int,
    ) : Goal()

    // ── fromState ─────────────────────────────────────────────────────────────
    companion object {
        fun fromState(
            typeName    : String,
            reward      : Long,
            targetLevel : Int,
            requirements: List<Combined.Requirement>,
            timeLimitSec: Int,
        ): Goal? = when (typeName) {
            Simple  ::class.simpleName -> Simple(reward, targetLevel)
            Combined::class.simpleName -> Combined(reward, requirements)
            Timed   ::class.simpleName -> Timed(reward, requirements, timeLimitSec)
            else                       -> null
        }
    }
}