package com.lewydo.idlemergecubes.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound

class SoundManager(var assetManager: AssetManager) {

    var loadableSoundList = mutableListOf<SoundData>()

    fun load() {
        loadableSoundList.onEach { assetManager.load(it.path, Sound::class.java) }
    }

    fun init() {
        loadableSoundList.onEach { it.sound = assetManager[it.path, Sound::class.java] }
        loadableSoundList.clear()
    }

    enum class EnumSound(val data: SoundData) {
        CLICK     (SoundData("sound/click.mp3")),
        CHECK_BOX (SoundData("sound/check_box.mp3")),

        BUY            (SoundData("sound/buy.mp3")),
        BUY_UPGRADE    (SoundData("sound/buy_upgrade.mp3")),
        COLLECT        (SoundData("sound/collect.mp3")),
        SHOW_COLLECT   (SoundData("sound/show_collect.mp3")),
        CUBE_TOUCH     (SoundData("sound/cube_touch.mp3")),
        LEVEL_UP       (SoundData("sound/level_up.mp3")),

        MERGE_1(SoundData("sound/merge_1.mp3")),
        MERGE_2(SoundData("sound/merge_2.mp3")),
        MERGE_3(SoundData("sound/merge_3.mp3")),

        GOALS_DONE(SoundData("sound/goals_done.mp3")),
        GOALS_FAIL(SoundData("sound/goals_fail.mp3")),
    }

    data class SoundData(
        val path: String,
    ) {
        lateinit var sound: Sound
    }

}