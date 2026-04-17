package com.lewydo.idlemergecubes.game.actors.progress

import com.badlogic.gdx.math.MathUtils
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.vfx.CircleProgressEffect
import com.lewydo.idlemergecubes.game.utils.vfx.VfxImage
import kotlin.math.abs

class ACircleProgress(
    override val screen: AdvancedScreen,
    progressPercent: Float  = 100f,
    var startAngle  : Float = 0f,
    var emptyPercent: Float = 0f,
) : VfxImage(
    screen  = screen,
    texture = screen.drawerUtil.getTexture(),
    effect  = CircleProgressEffect(
        progress   = progressPercent,
        startAngle = 0f,
        innerEmpty = 0f,
        roundness  = 1f,
        colorStart = GameColor.progressStart,
        colorEnd   = GameColor.progressEnd,
    ),
) {

    private val circleEffect get() = effect as CircleProgressEffect

    var progressPercent: Float = progressPercent
        private set

    private var targetProgress = progressPercent
    private val smoothSpeed    = 6f

    override fun act(delta: Float) {
        super.act(delta)
        // Синхронізуємо зовнішні var в ефект
        circleEffect.startAngle = startAngle
        circleEffect.innerEmpty = emptyPercent

        if (progressPercent == targetProgress) return
        progressPercent = MathUtils.lerp(progressPercent, targetProgress, smoothSpeed * delta)
        if (abs(progressPercent - targetProgress) < 0.01f) progressPercent = targetProgress
        circleEffect.progress = progressPercent
    }

    fun setProgress(percent: Float) {
        targetProgress = percent.coerceIn(-100f, 100f)
    }

    fun setProgressInstant(percent: Float) {
        val v = percent.coerceIn(-100f, 100f)
        targetProgress = v; progressPercent = v; circleEffect.progress = v
    }
}