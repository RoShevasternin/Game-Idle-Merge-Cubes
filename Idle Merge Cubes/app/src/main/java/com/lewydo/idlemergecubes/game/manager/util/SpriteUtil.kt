package com.lewydo.idlemergecubes.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.lewydo.idlemergecubes.game.manager.SpriteManager

class SpriteUtil {

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val cube           = getRegion("cube")
        val light_progress = getRegion("light_progress")
        val progress_frame = getRegion("progress_frame")
        val progress_line  = getRegion("progress_line")

        val BACKGROUND = SpriteManager.EnumTexture.BACKGROUND.data.texture
        val MASK       = SpriteManager.EnumTexture.MASK.data.texture

        private val C1 = SpriteManager.EnumTexture.C1.data.texture
        private val C2 = SpriteManager.EnumTexture.C2.data.texture
        private val C3 = SpriteManager.EnumTexture.C3.data.texture
        private val C4 = SpriteManager.EnumTexture.C4.data.texture
        private val C5 = SpriteManager.EnumTexture.C5.data.texture
        private val C6 = SpriteManager.EnumTexture.C6.data.texture

        val listLight = listOf(C1, C2, C3, C4, C5, C6).reversed()
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)


        // atlas All ------------------------------------------------------------------------------

        val coin            = getAllRegion("coin")
        val panel_lvl       = getAllRegion("panel_lvl")
        val settings_def    = getAllRegion("settings_def")
        val settings_press  = getAllRegion("settings_press")
        val buy_def         = getAllRegion("buy_def")
        val buy_press       = getAllRegion("buy_press")

        val cell_def   = getAllRegion("cell_def")
        val cell_green = getAllRegion("cell_green")
        val cell_red   = getAllRegion("cell_red")

        val cube = getAllRegion("cube")

        val listGlarePanelGame = List(4) { getAllRegion("glare_panel_game_${it.inc()}") }

        //val list = List(6) { getAllRegion("${it.inc()}") }

        // atlas 9_patch ------------------------------------------------------------------------------

        val panel_coin                     = get9Patch("panel_coin")
        val dialog_lvl                     = get9Patch("dialog_lvl")
        val progress_dialog_lvl            = get9Patch("progress_dialog_lvl")
        val progress_dialog_lvl_background = get9Patch("progress_dialog_lvl_background")

        // textures ------------------------------------------------------------------------------
        val MASK_DIALOG_PROGRESS_LVL = SpriteManager.EnumTexture.MASK_DIALOG_PROGRESS_LVL.data.texture
        val PANEL_TOP                = SpriteManager.EnumTexture.PANEL_TOP.data.texture
        val PANEL_GAME               = SpriteManager.EnumTexture.PANEL_GAME.data.texture

    }

}