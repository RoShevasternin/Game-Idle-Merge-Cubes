package com.lewydo.idlemergecubes.game.manager.util

import com.badlogic.gdx.audio.Sound
import com.lewydo.idlemergecubes.game.manager.AudioManager
import com.lewydo.idlemergecubes.game.utils.runGDX
import com.lewydo.idlemergecubes.game.manager.SoundManager
import kotlin.compareTo
import kotlin.div
import kotlin.times

class SoundUtil {

    val click = AdvancedSound(SoundManager.EnumSound.click.data.sound, 1f)

    val heart           = AdvancedSound(SoundManager.EnumSound.heart.data.sound, 1f)
    val hide            = AdvancedSound(SoundManager.EnumSound.hide.data.sound, 1f)
    val show_lewydo_tm  = AdvancedSound(SoundManager.EnumSound.show_lewydo_tm.data.sound, 0.7f)
    val show_libgdx     = AdvancedSound(SoundManager.EnumSound.show_libgdx.data.sound, 0.5f)
    val LibGDX          = AdvancedSound(SoundManager.EnumSound.LibGDX.data.sound, 0.33f)

    val touch = SoundManager.EnumSound.touch.data.sound

    private val boom1 = SoundManager.EnumSound.boom1.data.sound
    private val boom2 = SoundManager.EnumSound.boom2.data.sound
    private val boom3 = SoundManager.EnumSound.boom3.data.sound
    private val boom4 = SoundManager.EnumSound.boom4.data.sound
    private val boom5 = SoundManager.EnumSound.boom5.data.sound
    private val boom6 = SoundManager.EnumSound.boom6.data.sound
    private val boom7 = SoundManager.EnumSound.boom7.data.sound

    val boomList = listOf(boom1, boom2, boom3, boom4, boom5, boom6, boom7)

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