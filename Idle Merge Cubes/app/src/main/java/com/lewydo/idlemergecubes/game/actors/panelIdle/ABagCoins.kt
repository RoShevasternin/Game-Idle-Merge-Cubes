package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.vfx.BagCoinsEffect
import com.lewydo.idlemergecubes.game.utils.vfx.VfxImage

/**
 * Мішок з монетами — VfxImage + BagCoinsEffect.
 *
 * Раніше: VfxGroup (1 FBO рендер + 1 Blit) = 3 FBO ops per frame.
 * Тепер:  VfxImage (batch.shader в draw()) = 0 FBO ops per frame.
 *
 * bagCoinsFS.glsl читає тільки UV координату поточного пікселя —
 * не потребує бачити сусідів → FBO зайвий.
 */
class ABagCoins(override val screen: AdvancedScreen) : VfxImage(
    screen   = screen,
    region   = gdxGame.assetsAll.bag_coins,
    effect   = BagCoinsEffect(fillPercent = 0f),
) {
    private val bagEffect = effect as BagCoinsEffect

//    private var fillDuration = 10f
//    private var elapsed      = 0f
//    private var isFilling    = false

//    override fun act(delta: Float) {
//        super.act(delta)
//        if (!isFilling) return
//
//        elapsed += delta
//        bagEffect.fillPercent = (elapsed / fillDuration * 100f).coerceAtMost(100f)
//        if (bagEffect.fillPercent >= 100f) isFilling = false
//    }
//
//    fun startFill(seconds: Float) {
//        fillDuration          = seconds
//        elapsed               = 0f
//        bagEffect.fillPercent = 0f
//        isFilling             = true
//    }

//    fun reset() {
//        elapsed               = 0f
//        bagEffect.fillPercent = 0f
//        isFilling             = false
//    }

    // ── Новий метод — пряме виставлення прогресу ──────────────────────────────
    fun setProgress(pct: Float) {
        bagEffect.fillPercent = (pct * 100f).coerceIn(0f, 100f)
    }

    fun reset() {
        bagEffect.fillPercent = 0f
    }
}