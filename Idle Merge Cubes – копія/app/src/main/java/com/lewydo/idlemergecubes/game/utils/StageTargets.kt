package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.math.Vector2

object StageTargets {

    // ------------------------------------------------------------------------
    // Targets
    // ------------------------------------------------------------------------

    val coins = Vector2()
    val xp    = Vector2()

    // ------------------------------------------------------------------------
    // Register
    // ------------------------------------------------------------------------

    fun registerCoinsCenter(stageX: Float, stageY: Float) { coins.set(stageX, stageY) }
    fun registerXpCenter   (stageX: Float, stageY: Float) { xp.set(stageX, stageY)    }
}