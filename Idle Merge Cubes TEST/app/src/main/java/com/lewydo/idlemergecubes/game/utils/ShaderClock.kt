package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.Gdx

object ShaderClock {

    var time = 0f
        private set

    fun update(delta: Float = Gdx.graphics.deltaTime) {
        time = (time + delta) % 100f
    }
}