package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.math.Vector2

object GlobalStageTargets {

    enum class TargetType { COIN, XP }

    private val targetMap = Array(TargetType.entries.size) { Vector2() }

    fun registerTarget(type: TargetType, x: Float, y: Float) {
        targetMap[type.ordinal].set(x, y)
    }

    fun getTarget(type: TargetType): Vector2 = targetMap[type.ordinal]
}