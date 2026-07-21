package com.lewydo.idlemergecubes.game.manager.util

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Disposable
import com.lewydo.idlemergecubes.game.manager.AudioManager
import com.lewydo.idlemergecubes.game.manager.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// SoundUtil + саунд-директор (voice limiting, як у FMOD/Wwise).
//
//   1. THROTTLE per-sound: той самий звук не частіше ніж раз на throttleMs
//      (70мс дефолт). Вухо однакові звуки в цьому вікні зливає в один.
//   2. КАНАЛ + споживач на IO: GL-потік лише кладе запит (мікросекунди),
//      блокуючий sound.play() (SoundPool) виконується поза кадром.
//   3. DROP_LATEST при переповненні (8): при спамі зайві скидаються, а не
//      «доганяються» — інакше звук відстає від картинки.
//
//   Власний scope (як у MusicUtil) + Disposable: самодостатньо, не залежить
//   від порядку скасування gdxGame.coroutine.
// ─────────────────────────────────────────────────────────────────────────────

class SoundUtil : Disposable {

    private companion object { const val QUEUE_CAPACITY = 8 }

    val CLICK     = AdvancedSound(SoundManager.EnumSound.CLICK.data.sound, 1f)
    val CHECK_BOX = AdvancedSound(SoundManager.EnumSound.CHECK_BOX.data.sound, 1f)

    val BUY             = AdvancedSound(SoundManager.EnumSound.BUY.data.sound, 0.45f)
    val BUY_UPGRADE     = AdvancedSound(SoundManager.EnumSound.BUY_UPGRADE.data.sound, 0.45f)
    val COLLECT         = AdvancedSound(SoundManager.EnumSound.COLLECT.data.sound, 1f)
    val SHOW_COLLECT    = AdvancedSound(SoundManager.EnumSound.SHOW_COLLECT.data.sound, 1f)
    val CUBE_TOUCH      = AdvancedSound(SoundManager.EnumSound.CUBE_TOUCH.data.sound, 1f)
    val LEVEL_UP        = AdvancedSound(SoundManager.EnumSound.LEVEL_UP.data.sound, 1f)

    val MERGE_1 = AdvancedSound(SoundManager.EnumSound.MERGE_1.data.sound, 1f)
    val MERGE_2 = AdvancedSound(SoundManager.EnumSound.MERGE_2.data.sound, 1f)
    val MERGE_3 = AdvancedSound(SoundManager.EnumSound.MERGE_3.data.sound, 1f)

    val GOALS_DONE = AdvancedSound(SoundManager.EnumSound.GOALS_DONE.data.sound, 0.8f)
    val GOALS_FAIL = AdvancedSound(SoundManager.EnumSound.GOALS_FAIL.data.sound, 1f)

    // 0..100
    var volumeLevel = AudioManager.volumeLevelPercent

    var isPause = (volumeLevel <= 0f)

    // ── Директор ─────────────────────────────────────────────────────────────

    private val coroutine = CoroutineScope(Dispatchers.Default)

    private val channel = Channel<PlayRequest>(
        capacity = QUEUE_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_LATEST,   // спам → зайві геть
    )

    init {
        // Один споживач (послідовний). IO — бо sound.play() блокуючий.
        coroutine.launch(Dispatchers.IO) {
            for (req in channel) {
                runCatching { req.sound.play(req.volume) }   // не валимо цикл
            }
        }
    }

    /** Відтворити з тротлінгом. GL-потік НЕ блокується. */
    fun play(advancedSound: AdvancedSound, playCoff: Float = 1f) {
        if (isPause) return

        val now = System.currentTimeMillis()
        if (now - advancedSound.lastPlayMs < advancedSound.throttleMs) return
        advancedSound.lastPlayMs = now

        val volume = ((volumeLevel / 100f) * advancedSound.coff) * playCoff
        channel.trySend(PlayRequest(advancedSound.sound, volume))
    }

    override fun dispose() {
        channel.close()
        coroutine.cancel()
    }

    private class PlayRequest(val sound: Sound, val volume: Float)

    /** coff — гучність; throttleMs — мін. інтервал між повторами ЦЬОГО звуку. */
    class AdvancedSound(
        val sound: Sound,
        val coff : Float,
        val throttleMs: Long = 70L,
    ) {
        internal var lastPlayMs = 0L
    }

}