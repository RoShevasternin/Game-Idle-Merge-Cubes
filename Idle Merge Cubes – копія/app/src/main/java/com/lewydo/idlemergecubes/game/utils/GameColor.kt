package com.lewydo.idlemergecubes.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background    :Color = Color.valueOf("3A0E7A")
    val yellow_FFF858 :Color = Color.valueOf("FFF858")
    val purple_350080 :Color = Color.valueOf("350080")
    val brown_8D3800  :Color = Color.valueOf("8D3800")
    val green_98FF68  :Color = Color.valueOf("98FF68")

    val progressStart :Color = Color.valueOf("EAFF00")
    val progressEnd   :Color = Color.valueOf("FFA600")

    val white_55 : Color = Color.WHITE.cpy().apply { a = 0.55f }
    val black_55 : Color = Color.BLACK.cpy().apply { a = 0.55f }
}