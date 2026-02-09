package com.lewydo.rozval.game.manager.util

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.lewydo.rozval.game.manager.SpriteManager

class SpriteUtil {

     class Loader {
          private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

          val loader = getRegion("loader")

          val builderList = List(4) { getRegion("builder-${it.inc()}") }
     }

     class All {
         private fun getAllRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
         private fun getThanksRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.THANKS.data.atlas.findRegion(name)
         private fun getLevelBtnRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LEVEL_BTN.data.atlas.findRegion(name)

         private fun getNinePath(name: String): NinePatch = SpriteManager.EnumAtlas.ALL.data.atlas.createPatch(name)

         // atlas All ------------------------------------------------------------------------------

         val built_with   = getThanksRegion("built_with")
         val lewydo_heart = getThanksRegion("lewydo_heart")
         val Lewydo_TM    = getThanksRegion("Lewydo_TM")
         val libGDX       = getThanksRegion("libGDX")

         // atlas All ------------------------------------------------------------------------------

         val market_def          = getAllRegion("market_def")
         val market_press        = getAllRegion("market_press")
         val get_keys_def        = getAllRegion("get_keys_def")
         val get_keys_press      = getAllRegion("get_keys_press")
         val exit_def            = getAllRegion("exit_def")
         val exit_press          = getAllRegion("exit_press")
         val menu_def            = getAllRegion("menu_def")
         val menu_press          = getAllRegion("menu_press")
         val music_check         = getAllRegion("music_check")
         val music_def           = getAllRegion("music_def")
         val sound_check         = getAllRegion("sound_check")
         val sound_def           = getAllRegion("sound_def")
         val apply_def           = getAllRegion("apply_def")
         val apply_press         = getAllRegion("apply_press")
         val progress_arm        = getAllRegion("progress_arm")
         val progress_background = getAllRegion("progress_background")
         val progress_progress   = getAllRegion("progress_progress")
         val lock_def            = getAllRegion("lock_def")
         val lock_press          = getAllRegion("lock_press")
         val star_empty          = getAllRegion("star_empty")
         val star_fill           = getAllRegion("star_fill")
         val star_grey           = getAllRegion("star_grey")
         val key_fill            = getAllRegion("key_fill")
         val key_grey            = getAllRegion("key_grey")
         val lvl                 = getAllRegion("lvl")

         val lvl_btn_dis   = getNinePath("lvl_btn_dis")
         val lvl_btn_press = getNinePath("lvl_btn_press")
         val lvl_btn_gold  = getNinePath("lvl_btn_gold")

         // atlas LevelBtn ------------------------------------------------------------------------------
         val list_LvlBtnDef      = List(14) { getLevelBtnRegion("lvl_btn_def_${it.inc()}") }
         val list_PreviewLvl     = List(14) { getLevelBtnRegion("preview_lvl_${it.inc()}") }

         // textures ------------------------------------------------------------------------------
         val PANEL         = SpriteManager.EnumTexture.PANEL.data.texture
         val PROGRESS_MASK = SpriteManager.EnumTexture.PROGRESS_MASK.data.texture

         val LEVEL_SEPARATOR            = SpriteManager.EnumTexture.LEVEL_SEPARATOR.data.texture
         val LEVEL_SEPARATOR_LOCK_DEF   = SpriteManager.EnumTexture.LEVEL_SEPARATOR_LOCK_DEF.data.texture
         val LEVEL_SEPARATOR_LOCK_PRESS = SpriteManager.EnumTexture.LEVEL_SEPARATOR_LOCK_PRESS.data.texture

         private val LVL_1 = SpriteManager.EnumTexture.LVL_1.data.texture

         val listBackgroundLVL = listOf(
             LVL_1,
         )


         // TEST
         val MASK_CIRCLE = SpriteManager.EnumTexture.MASK_CIRCLE.data.texture
         val HOR         = SpriteManager.EnumTexture.HOR.data.texture
         val VER         = SpriteManager.EnumTexture.VER.data.texture
         val BRICK       = SpriteManager.EnumTexture.BRICK.data.texture
         val WOOD        = SpriteManager.EnumTexture.WOOD.data.texture
         val RESET       = SpriteManager.EnumTexture.RESET.data.texture
         val GRID        = SpriteManager.EnumTexture.GRID.data.texture
     }

}