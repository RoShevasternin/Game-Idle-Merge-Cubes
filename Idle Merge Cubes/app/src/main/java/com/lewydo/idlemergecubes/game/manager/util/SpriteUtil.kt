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
        private fun getGridRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.GRID.data.atlas.findRegion(name)
        private fun getMenuRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.MENU.data.atlas.findRegion(name)

        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val coin            = getAllRegion("coin")
        val panel_lvl       = getAllRegion("panel_lvl")
        val settings_def    = getAllRegion("settings_def")
        val settings_press  = getAllRegion("settings_press")
        val buy_def         = getAllRegion("buy_def")
        val buy_press       = getAllRegion("buy_press")

        // Idle
        val bag_coins                = getAllRegion("bag_coins")
        val idle_progress            = getAllRegion("idle_progress")
        val idle_progress_background = getAllRegion("idle_progress_background")
        val collect_def              = getAllRegion("collect_def")
        val collect_press            = getAllRegion("collect_press")
        val collect_x2_def           = getAllRegion("collect_x2_def")
        val collect_x2_press         = getAllRegion("collect_x2_press")

        val listGlarePanelGame = List(4) { getAllRegion("glare_panel_game_${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS GRID
        // ------------------------------------------------------------------------------
        val cell_def   = getGridRegion("cell_def")
        val cell_green = getGridRegion("cell_green")
        val cell_red   = getGridRegion("cell_red")
        val cell_tint  = getGridRegion("cell_tint")

        val listCube = List(3) { getGridRegion("cube${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS MENU
        // ------------------------------------------------------------------------------
        val close_def               = getMenuRegion("close_def")
        val close_press             = getMenuRegion("close_press")
        val expand                  = getMenuRegion("expand")
        val menu_icon_leaderboard   = getMenuRegion("menu_icon_leaderboard")
        val menu_icon_settings      = getMenuRegion("menu_icon_settings")
        val menu_item_section_def   = getMenuRegion("menu_item_section_def")
        val menu_item_section_press = getMenuRegion("menu_item_section_press")
        val reset_game_def          = getMenuRegion("reset_game_def")
        val reset_game_press        = getMenuRegion("reset_game_press")
        val settings_separator      = getMenuRegion("settings_separator")
        val box_off                 = getMenuRegion("box_off")
        val icons_alarm             = getMenuRegion("icons_alarm")
        val icons_info              = getMenuRegion("icons_info")
        val icons_music             = getMenuRegion("icons_music")
        val icons_sound             = getMenuRegion("icons_sound")
        val icons_vibro             = getMenuRegion("icons_vibro")
        val settings_item           = getMenuRegion("settings_item")
        val music_box_on            = getMenuRegion("music_box_on")
        val sound_box_on            = getMenuRegion("sound_box_on")
        val vibro_box_on            = getMenuRegion("vibro_box_on")
        val alarm_box_on            = getMenuRegion("alarm_box_on")

        // ------------------------------------------------------------------------------
        // ATLAS 9_PATCH
        // ------------------------------------------------------------------------------

        val panel_coin                     = get9Patch("panel_coin")
        val dialog_lvl                     = get9Patch("dialog_lvl")
        val progress_dialog_lvl            = get9Patch("progress_dialog_lvl")
        val progress_dialog_lvl_background = get9Patch("progress_dialog_lvl_background")
        val panel_settings                 = get9Patch("panel_settings")

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        val MASK_DIALOG_PROGRESS_LVL = SpriteManager.EnumTexture.MASK_DIALOG_PROGRESS_LVL.data.texture
        val MASK_PROGRESS_IDLE       = SpriteManager.EnumTexture.MASK_PROGRESS_IDLE.data.texture
        val PANEL_TOP                = SpriteManager.EnumTexture.PANEL_TOP.data.texture
        val PANEL_GAME               = SpriteManager.EnumTexture.PANEL_GAME.data.texture
        val PANEL_IDLE               = SpriteManager.EnumTexture.PANEL_IDLE.data.texture
        val PANEL_MENU               = SpriteManager.EnumTexture.PANEL_MENU.data.texture

    }

}