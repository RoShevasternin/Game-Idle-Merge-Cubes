package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.lewydo.idlemergecubes.game.utils.ShaderClock
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.vfx.LavaProgressEffect
import com.lewydo.idlemergecubes.game.utils.vfx.VfxImage

class ALavaProgress(override val screen: AdvancedScreen) : VfxImage(
    screen = screen,
    region = gdxGame.assetsAll.idle_progress,
    effect = LavaProgressEffect()
) {

    private val lavaEffect get() = effect as LavaProgressEffect

    private var finishing   = false
    private var finishTimer = 0f

    override fun act(delta: Float) {
        super.act(delta)
        lavaEffect.time = ShaderClock.time

        if (!finishing) return
        finishTimer += delta
        val progress = (finishTimer / 0.4f).coerceAtMost(1f)
        lavaEffect.edgeDeform  = 1f - progress
        lavaEffect.finishFlash = when {
            finishTimer < 0.12f -> finishTimer / 0.12f
            else                -> 1f - ((finishTimer - 0.12f) / 0.35f).coerceAtMost(1f)
        }
        if (progress >= 1f) finishing = false
    }

    fun reset() {
        finishing              = false
        finishTimer            = 0f
        lavaEffect.edgeDeform  = 1f
        lavaEffect.finishFlash = 0f
    }

    fun finish() {
        finishing   = true
        finishTimer = 0f
    }
}