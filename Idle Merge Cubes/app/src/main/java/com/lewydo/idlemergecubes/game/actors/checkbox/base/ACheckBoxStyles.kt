package com.lewydo.idlemergecubes.game.actors.checkbox.base

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lewydo.idlemergecubes.game.utils.gdxGame

object ACheckBoxStyles {
    val MUSIC get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.box_off),
        checked = TextureRegionDrawable(gdxGame.assetsAll.music_box_on),
    )
    val SOUND get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.box_off),
        checked = TextureRegionDrawable(gdxGame.assetsAll.sound_box_on),
    )
    val VIBRO get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.box_off),
        checked = TextureRegionDrawable(gdxGame.assetsAll.vibro_box_on),
    )
    val ALARM get() = ACheckBox.Style(
        default = TextureRegionDrawable(gdxGame.assetsAll.box_off),
        checked = TextureRegionDrawable(gdxGame.assetsAll.alarm_box_on),
    )
}