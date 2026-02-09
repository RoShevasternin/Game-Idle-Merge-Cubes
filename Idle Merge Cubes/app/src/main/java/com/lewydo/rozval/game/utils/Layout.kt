package com.lewydo.rozval.game.utils

import com.badlogic.gdx.math.Vector2

object Layout {

    object Loader {
        val loader        = LayoutData(707f, 288f, 505f, 505f)
        val loading       = LayoutData(770f, 103f, 197f, 78f)
        val loadingPoints = LayoutData(967f, 103f, 38f, 78f)
        val progress      = LayoutData(1018f, 103f, 132f, 78f)
        val builder       = LayoutData(860f, 383f, 200f, 315f)
    }

    data class LayoutData(
        val x: Float = 0f,
        val y: Float = 0f,
        val w: Float = 0f,
        val h: Float = 0f,
        // horizontal space
        val hs: Float = 0f,
        // vertical space
        val vs: Float = 0f,
    ) {

        fun position() = Vector2(x, y)
        fun size()     = Vector2(w, h)

    }

}












