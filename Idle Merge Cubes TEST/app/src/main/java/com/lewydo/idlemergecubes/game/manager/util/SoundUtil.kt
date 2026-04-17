package com.lewydo.idlemergecubes.game.manager.util

import com.badlogic.gdx.audio.Sound
import com.lewydo.idlemergecubes.game.manager.AudioManager
import com.lewydo.idlemergecubes.game.manager.SoundManager

class SoundUtil {

    val CLICK     = AdvancedSound(SoundManager.EnumSound.CLICK.data.sound, 1f)
    val CHECK_BOX = AdvancedSound(SoundManager.EnumSound.CHECK_BOX.data.sound, 1f)

    val BUY             = AdvancedSound(SoundManager.EnumSound.BUY.data.sound, 0.45f)
    val COLLECT         = AdvancedSound(SoundManager.EnumSound.COLLECT.data.sound, 1f)
    val SHOW_COLLECT    = AdvancedSound(SoundManager.EnumSound.SHOW_COLLECT.data.sound, 1f)
    val CUBE_TOUCH      = AdvancedSound(SoundManager.EnumSound.CUBE_TOUCH.data.sound, 1f)
    val LEVEL_UP        = AdvancedSound(SoundManager.EnumSound.LEVEL_UP.data.sound, 1f)


    val MERGE_1 = AdvancedSound(SoundManager.EnumSound.MERGE_1.data.sound, 1f)
    val MERGE_2 = AdvancedSound(SoundManager.EnumSound.MERGE_2.data.sound, 1f)
    val MERGE_3 = AdvancedSound(SoundManager.EnumSound.MERGE_3.data.sound, 1f)

    // 0..100
    var volumeLevel = AudioManager.volumeLevelPercent

    var isPause = (volumeLevel <= 0f)

    fun play(advancedSound: AdvancedSound, playCoff: Float = 1f) {
        if (isPause.not()) {
            advancedSound.apply {
                sound.play(((volumeLevel / 100f) * coff) * playCoff)
            }
        }
    }

    data class AdvancedSound(val sound: Sound, val coff: Float)
}