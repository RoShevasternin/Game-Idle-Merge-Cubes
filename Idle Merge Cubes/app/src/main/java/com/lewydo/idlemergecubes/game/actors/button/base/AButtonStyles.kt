package com.lewydo.idlemergecubes.game.actors.button.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture.Style
import com.lewydo.idlemergecubes.game.utils.TextureEmpty
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.region

object AButtonStyles {

    // ------------------------------------------------------------------------
    // AButtonTexture.Style
    // ------------------------------------------------------------------------

    object Texture {
        val NONE get() = Style(default = TextureRegionDrawable(TextureEmpty.region))

        val SETTINGS
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.settings_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.settings_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.settings_press),
            )
        val BUY
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.buy_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.buy_def),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.buy_dis),
            )
        val COLLECT
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.collect_frame_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.collect_frame_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.collect_frame_press),
            )
        val BACK
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.back_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.back_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.back_press),
            )
        val MENU_ITEM
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.menu_item_section_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.menu_item_section_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.menu_item_section_press),
            )
        val MENU_RESET_GAME
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.reset_game_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.reset_game_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.reset_game_press),
            )
        val MENU_CLOSE
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.close_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.close_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.close_press),
            )
        val YES
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.yes_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.yes_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.yes_press),
            )
        val NO
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.no_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.no_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.no_press),
            )
        val LEADERBOARD
            get() = Style(
                default = TextureRegionDrawable(gdxGame.assetsAll.leaderboard_def),
                pressed = TextureRegionDrawable(gdxGame.assetsAll.leaderboard_press),
                disabled = TextureRegionDrawable(gdxGame.assetsAll.leaderboard_press),
            )
    }

    // ------------------------------------------------------------------------
    // AButtonAnim.Style
    // ------------------------------------------------------------------------

    object Anim {
        val NONE get() = AButtonAnim.Style(TextureRegionDrawable(TextureEmpty.region))
    }

    // All ------------------------------------------------------------------------
    //val DAILY_CONVERTER_ITEM           get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_converter_item))
    //val DAILY_FREE_RBX_CALCULATOR_ITEM get() = AButtonAnim.Style(TextureRegionDrawable(gdxGame.assetsAll.daily_free_rbx_calculator_item))

    // ------------------------------------------------------------------------
    // AButtonAnimTexture.Style
    // ------------------------------------------------------------------------
    object AnimTexture {
        val NONE get() = AButtonAnimTexture.Style(TextureRegionDrawable(TextureEmpty.region))

//        val GOLDEN get() = AButtonAnimTexture.Style(
//            default  = TextureRegionDrawable(gdxGame.assetsAll.golden_def),
//            disabled = TextureRegionDrawable(gdxGame.assetsAll.golden_dis),
//        )
    }

}