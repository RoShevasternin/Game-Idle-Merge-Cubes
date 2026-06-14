package com.lewydo.idlemergecubes.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.lewydo.idlemergecubes.game.manager.SpriteManager

class SpriteUtil {

    class Brand {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.BRAND.data.atlas.findRegion(name)

        val brand_back  = getRegion("brand_back")
        val brand_front = getRegion("brand_front")
        val brand_line  = getRegion("brand_line")
        val lewydo      = getRegion("lewydo")
        val slogan      = getRegion("slogan")

        val BACKGROUND = SpriteManager.EnumTexture.BRAND_BACKGROUND.data.texture
    }

    class Loader {
        private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

        val cube           = getRegion("cube")
        val light_progress = getRegion("light_progress")
        val progress_frame = getRegion("progress_frame")
        val progress_line  = getRegion("progress_line")

        val BACKGROUND = SpriteManager.EnumTexture.BACKGROUND.data.texture
        val MASK       = SpriteManager.EnumTexture.MASK.data.texture

        val listLight = SpriteManager.EnumTextureGroup.LIGHT_C.data.textures.reversed()
    }

    class All {
        private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
        private fun getGridRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.GRID.data.atlas.findRegion(name)
        private fun getMenuRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.MENU.data.atlas.findRegion(name)

        private fun get9Patch(name: String): NinePatch = SpriteManager.EnumAtlas._9_PATCH.data.atlas.createPatch(name)

        // ------------------------------------------------------------------------------
        // ATLAS ALL
        // ------------------------------------------------------------------------------

        val coin                            = getAllRegion("coin")
        val coin_with_border                = getAllRegion("coin_with_border")
        val cube_buy                        = getAllRegion("cube_buy")
        val panel_lvl                       = getAllRegion("panel_lvl")
        val settings_def                    = getAllRegion("settings_def")
        val settings_press                  = getAllRegion("settings_press")
        val buy_def                         = getAllRegion("buy_def")
        val buy_dis                         = getAllRegion("buy_dis")
        val no_def                          = getAllRegion("no_def")
        val no_press                        = getAllRegion("no_press")
        val yes_def                         = getAllRegion("yes_def")
        val yes_press                       = getAllRegion("yes_press")
        val glare_collect_left              = getAllRegion("glare_collect_left")
        val glare_collect_right             = getAllRegion("glare_collect_right")
        val x2                              = getAllRegion("x2")
        val bag_coins                       = getAllRegion("bag_coins")
        val progress_merge_bonus            = getAllRegion("progress_merge_bonus")
        val progress_background_merge_bonus = getAllRegion("progress_background_merge_bonus")
        val collect_center                  = getAllRegion("collect_center")
        val collect_center_x2               = getAllRegion("collect_center_x2")
        val collect_frame_def               = getAllRegion("collect_frame_def")
        val collect_frame_press             = getAllRegion("collect_frame_press")
        val circle_fill                     = getAllRegion("offline_circle_fill")
        val circle_stroke                   = getAllRegion("offline_circle_stroke")
        val back_def                        = getAllRegion("back_def")
        val back_press                      = getAllRegion("back_press")
        val tutorial_hand                   = getAllRegion("tutorial_hand")
        val lock                            = getAllRegion("lock")
        val progress_buy_hint               = getAllRegion("progress_buy_hint")
        val progress_frame_buy_hint         = getAllRegion("progress_frame_buy_hint")
        val icon_timer                      = getAllRegion("icon_timer")
        val goals_item_done                 = getAllRegion("goals_item_done")
        val goals_progress                  = getAllRegion("goals_progress")
        val goals_progress_bg               = getAllRegion("goals_progress_bg")

        val listGlarePanelGame = List(4) { getAllRegion("glare_panel_game_${it.inc()}") }

        // ------------------------------------------------------------------------------
        // ATLAS GRID
        // ------------------------------------------------------------------------------
        val cell_def   = getGridRegion("cell_def")
        val cell_green = getGridRegion("cell_green")
        val cell_red   = getGridRegion("cell_red")
        val cell_tint  = getGridRegion("cell_tint")

        val cube = getGridRegion("cube")

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
        val panel_who                      = get9Patch("panel_who")
        val panel_merge_bonus              = get9Patch("panel_merge_bonus")
        val separator                      = get9Patch("separator")
        val separator_2                    = get9Patch("separator_2")
        val goals_badge                    = get9Patch("goals_badge")
        val goals_pill_reward              = get9Patch("goals_pill_reward")
        val goals_pill_timer               = get9Patch("goals_pill_timer")
        val goals_pill_timer_red           = get9Patch("goals_pill_timer_red")
        val goals_bg_item_def              = get9Patch("goals_bg_item_def")
        val goals_bg_item_done             = get9Patch("goals_bg_item_done")

        // ------------------------------------------------------------------------------
        // TEXTURES
        // ------------------------------------------------------------------------------

        // TEST
        //val bg_test    = SpriteManager.EnumTexture.bg_test.data.texture
        val green      = SpriteManager.EnumTexture.green.data.texture
        val red        = SpriteManager.EnumTexture.red.data.texture
        val ComingSoon = SpriteManager.EnumTexture.ComingSoon.data.texture

        // ALL
        val LIGHT    = SpriteManager.EnumTexture.LIGHT.data.texture
        val COIN_BIG = SpriteManager.EnumTexture.COIN_BIG.data.texture
        val CONFETTI = SpriteManager.EnumTexture.CONFETTI.data.texture

        // BRAND
        val BRAND       = SpriteManager.EnumTexture.BRAND.data.texture
        val BRAND_BACK  = SpriteManager.EnumTexture.BRAND_BACK.data.texture
        val BRAND_FRONT = SpriteManager.EnumTexture.BRAND_FRONT.data.texture
        val LILY        = SpriteManager.EnumTexture.LILY.data.texture
        val VELDAN      = SpriteManager.EnumTexture.VELDAN.data.texture

        // MASK
        val MASK_DIALOG_PROGRESS_LVL  = SpriteManager.EnumTexture.MASK_DIALOG_PROGRESS_LVL.data.texture
        val MASK_PROGRESS_MERGE_BONUS = SpriteManager.EnumTexture.MASK_PROGRESS_MERGE_BONUS.data.texture
        val MASK_DIALOG_OFFLINE       = SpriteManager.EnumTexture.MASK_DIALOG_OFFLINE.data.texture
        val MASK_DIALOG_LEVEL_UP      = SpriteManager.EnumTexture.MASK_DIALOG_LEVEL_UP.data.texture
        val MASK_PROGRESS_BUY_HINT    = SpriteManager.EnumTexture.MASK_PROGRESS_BUY_HINT.data.texture
        val MASK_GOALS_PROGRESS       = SpriteManager.EnumTexture.MASK_GOALS_PROGRESS.data.texture

        // PANEL
        val PANEL_TOP                = SpriteManager.EnumTexture.PANEL_TOP.data.texture
        val PANEL_GAME               = SpriteManager.EnumTexture.PANEL_GAME.data.texture
        val PANEL_MENU               = SpriteManager.EnumTexture.PANEL_MENU.data.texture
        val PANEL_LEVEL_UP_BONUS     = SpriteManager.EnumTexture.PANEL_LEVEL_UP_BONUS.data.texture

        // DIALOG
        val DIALOG_CLEAR_GRID = SpriteManager.EnumTexture.DIALOG_CLEAR_GRID.data.texture
        val DIALOG_OFFLINE    = SpriteManager.EnumTexture.DIALOG_OFFLINE.data.texture
        val DIALOG_LEVEL_UP   = SpriteManager.EnumTexture.DIALOG_LEVEL_UP.data.texture

        // All | goals
        val BG_COMBINED = SpriteManager.EnumTexture.BG_COMBINED.data.texture
        val BG_SIMPLE   = SpriteManager.EnumTexture.BG_SIMPLE.data.texture
        val BG_TIMED    = SpriteManager.EnumTexture.BG_TIMED.data.texture
    }

}