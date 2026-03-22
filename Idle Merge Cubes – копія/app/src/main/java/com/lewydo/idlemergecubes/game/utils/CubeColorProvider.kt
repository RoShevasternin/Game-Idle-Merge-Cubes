package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.graphics.Color

object CubeColorSystem {

    // ---------- MANUAL FIRST 10 ----------

    private val cubeHexColors = mapOf(
        1 to "8A4DFF", //"FF730F",  // Premium Purple старт
        2 to "4B6BFF",
        3 to "2EC5FF",
        4 to "30F0D0",
        5 to "35E06F",
        6 to "A4F030",
        7 to "FFD84A",
        8 to "FF9B23",
        9 to "FF3B3B",
        10 to "FF4FC3"
    )

    private val frameHexColors = mapOf(
        1 to "5B2ED6", //"FFFFFF",
        2 to "2E45CC",
        3 to "1E8FCC",
        4 to "00BFA5",
        5 to "1AA64B",
        6 to "7CB518",
        7 to "D6A400",
        8 to "CC6E00",
        9 to "B00020",
        10 to "C2185B"
    )

    // ---------- PUBLIC API ----------

    fun getCubeColor(level: Int): Color {
        cubeHexColors[level]?.let { return Color.valueOf(it) }
        return proceduralColor(level)
    }

    fun getFrameColor(level: Int): Color {
        frameHexColors[level]?.let { return Color.valueOf(it) }
        return proceduralFrameColor(level)
    }

    // ---------- PROCEDURAL AFTER 10 ----------

    private const val GOLDEN_RATIO = 0.618034f

    private fun proceduralColor(level: Int): Color {

        val n = level.toFloat()

        val hue = ((n * GOLDEN_RATIO) % 1f) * 360f
        val progression = (level / 200f).coerceAtMost(1f)

        val saturation = lerp(0.8f, 0.95f, progression)
        val value = lerp(1f, 0.8f, progression)

        return Color().apply {
            fromHsv(hue, saturation, value)
            a = 1f
        }
    }

    private fun proceduralFrameColor(level: Int): Color {

        val cube = proceduralColor(level)
        val hsv = FloatArray(3)
        cube.toHsv(hsv)

        return Color().apply {
            fromHsv(
                hsv[0],
                (hsv[1] * 1.1f).coerceAtMost(1f),
                hsv[2] * 0.6f
            )
            a = 1f
        }
    }

    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t
    }
}