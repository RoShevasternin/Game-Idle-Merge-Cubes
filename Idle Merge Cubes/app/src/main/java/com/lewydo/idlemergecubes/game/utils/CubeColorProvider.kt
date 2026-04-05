package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.graphics.Color

object CubeColorSystem {

    // Рівні 1-20 вручну — ігровий тір-прогрес
    // Правило: warm старт → cool середина → special фінал
    // AHslImage бере тільки hue+sat, тому lightness hex не важлива
    private val cubeHexColors = mapOf(
        1  to "FF8126",  // Orange   — стартовий
        2  to "F0C030",  // Gold     — тепло, апгрейд
        3  to "90D000",  // Lime     — перший "cool"
        4  to "10B860",  // Emerald  — природній
        5  to "00C0B0",  // Teal     — milestone відчуття
        6  to "1878E8",  // Sapphire — рідкісний
        7  to "9020D8",  // Amethyst — преміум
        8  to "D01890",  // Orchid   — ексклюзив
        9  to "D02020",  // Ruby     — небезпечний
        10 to "E8D020",  // Divine   — milestone ✦
    )

    fun getCubeColor(level: Int): Color {
        cubeHexColors[level]?.let { return Color.valueOf(it) }
        return proceduralColor(level)
    }

    // Після рівня 20 — процедурна генерація
    private const val GOLDEN_RATIO = 0.618034f

    private fun proceduralColor(level: Int): Color {
        val hue = ((level * GOLDEN_RATIO) % 1f) * 360f
        val saturation = 0.9f
        val value = if (level % 5 == 0) 1f else 0.85f // кожен 5-й яскравіший
        return Color().apply {
            fromHsv(hue, saturation, value)
            a = 1f
        }
    }
}