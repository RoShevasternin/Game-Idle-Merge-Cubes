package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val white_E5FFE0  :Color = Color.valueOf("E5FFE0")
    val background    :Color = Color.valueOf("3A0E7A")
    val purple_350080 :Color = Color.valueOf("350080")
    val blue_3A44FF   :Color = Color.valueOf("3A44FF")
    val yellow_FFF858 :Color = Color.valueOf("FFF858")
    val brown_8D3800  :Color = Color.valueOf("8D3800")
    val brown_683E03  :Color = Color.valueOf("683E03")
    val green_00B252  :Color = Color.valueOf("00B252")
    val green_98FF68  :Color = Color.valueOf("98FF68")
    val red_E22057    :Color = Color.valueOf("E22057")

    val dark_brown_360000 :Color = Color.valueOf("360000")

    val progressStart :Color = Color.valueOf("EAFF00")
    val progressEnd   :Color = Color.valueOf("FFA600")

    val green_66 : Color = Color.valueOf("00FF66").apply { a = 0.66f }

    val white_55 : Color = Color.WHITE.cpy().apply { a = 0.55f }
    val black_55 : Color = Color.BLACK.cpy().apply { a = 0.55f }
}