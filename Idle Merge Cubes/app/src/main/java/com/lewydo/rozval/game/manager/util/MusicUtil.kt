package com.lewydo.rozval.game.manager.util

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.utils.Disposable
import com.lewydo.rozval.game.manager.AudioManager
import com.lewydo.rozval.game.utils.runGDX
import com.lewydo.rozval.util.cancelCoroutinesAll
import com.lewydo.rozval.game.manager.MusicManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.div
import kotlin.times

class MusicUtil: Disposable {

    private val coroutine = CoroutineScope(Dispatchers.Default)

    val game     = MusicManager.EnumMusic.game.data.music
    val main     = MusicManager.EnumMusic.main.data.music
    val roulette = MusicManager.EnumMusic.roulette.data.music

    // 0..100
    val volumeLevelFlow = MutableStateFlow(AudioManager.volumeLevelPercent)

    var coff = 1f

    private var lastMusic: Music? = null
    var currentMusic: Music?
        get() = lastMusic
        set(value) { runGDX {
            if (value != null) {
                if (lastMusic != value) {
                    lastMusic?.stop()
                    lastMusic = value
                    lastMusic?.volume = (volumeLevelFlow.value / 100f) * coff
                    lastMusic?.play()
                }
            } else {
                lastMusic?.stop()
                lastMusic = null
            }
        } }

    init {
        coroutine.launch { volumeLevelFlow.collect { level -> runGDX { lastMusic?.volume = (level / 100f) * coff } } }
    }

    override fun dispose() {
        cancelCoroutinesAll(coroutine)
        lastMusic = null
        currentMusic  = null
    }

}