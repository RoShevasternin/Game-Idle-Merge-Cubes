package com.lewydo.idlemergecubes.game.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import java.lang.reflect.Field

class AParticleEffectActor(
    val particleEffect: ParticleEffect,
    private val resetOnStart: Boolean = false
) : Actor(), Disposable {

    var isRunning = true
        private set

    // Кешуємо посилання на внутрішні масиви емітерів, щоб забути про рефлексію в циклі
    private class EmitterData(
        val particles: Array<out Sprite>,
        val active: BooleanArray
    )

    private val emittersData: Array<EmitterData> = particleEffect.emitters.map { emitter ->
        val pField = emitter.javaClass.getDeclaredField("particles").apply { isAccessible = true }
        val aField = emitter.javaClass.getDeclaredField("active").apply { isAccessible = true }
        EmitterData(
            pField.get(emitter) as Array<out Sprite>,
            aField.get(emitter) as BooleanArray
        )
    }.toTypedArray()

    private data class FloatRange(val lowMin: Float, val lowMax: Float, val highMin: Float, val highMax: Float)
    private val originalAngles = particleEffect.emitters.map { emitter ->
        val a = emitter.angle
        FloatRange(a.lowMin, a.lowMax, a.highMin, a.highMax)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (isRunning) {
            particleEffect.update(delta)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (!isRunning) return

        val finalAlpha = color.a * parentAlpha

        // Оптимізація: якщо альфа 1.0, малюємо як є.
        // Якщо 0.0 — взагалі не малюємо.
        if (finalAlpha <= 0.01f) return

        if (finalAlpha < 0.99f) {
            applyAlphaToEmitters(finalAlpha)
        }

        particleEffect.draw(batch)
    }

    private fun applyAlphaToEmitters(finalAlpha: Float) {
        // Тепер тут немає жодної рефлексії. Тільки швидкий прохід по масивах.
        for (data in emittersData) {
            val particles = data.particles
            val active = data.active

            for (i in active.indices) {
                if (active[i]) {
                    val p = particles[i]
                    // ParticleEmitter.update() вже встановив "базову" альфу частинки.
                    // Ми просто множимо її на альфу актора.
                    p.setAlpha(p.color.a * finalAlpha)
                }
            }
        }
    }

    // --- Решта логіки (rotation, position, scale) залишається без змін ---

    override fun rotationChanged() {
        particleEffect.emitters.forEachIndexed { i, emitter ->
            val a = originalAngles[i]
            emitter.angle.setLow(a.lowMin + rotation, a.lowMax + rotation)
            emitter.angle.setHigh(a.highMin + rotation, a.highMax + rotation)
        }
    }

    override fun positionChanged() {
        particleEffect.setPosition(x, y)
    }

    override fun scaleChanged() {
        particleEffect.scaleEffect(scaleX, scaleY, scaleY)
    }

    fun start() {
        if (resetOnStart) particleEffect.reset(false)
        particleEffect.start()
    }

    fun pause() { isRunning = false }
    fun resume() { isRunning = true }
    fun allowCompletion() = particleEffect.allowCompletion()
    override fun dispose() = particleEffect.dispose()
}