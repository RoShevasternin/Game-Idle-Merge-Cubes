package com.lewydo.rozval.game.utils.advanced

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.lewydo.rozval.game.utils.SizeScaler
import com.lewydo.rozval.game.utils.disposeAll
import com.lewydo.rozval.util.cancelCoroutinesAll
import com.lewydo.rozval.util.currentClassName
import com.lewydo.rozval.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.atomic.AtomicBoolean

abstract class AdvancedGroup : WidgetGroup(), Disposable {
    abstract val screen: AdvancedScreen

    open val sizeScaler: SizeScaler = SizeScaler(SizeScaler.Axis.X, 1f)

    var coroutine: CoroutineScope? = CoroutineScope(Dispatchers.Default)
        private set
    var isDisposed = false
        private set

    var isDisposeOnRemove = true

    val preDrawArray  = Array<Drawer>()
    val postDrawArray = Array<Drawer>()
    val disposableSet = mutableSetOf<Disposable>()

    val Vector2.toActual get() = sizeScaler.toActual(this)
    val Vector2.toDesign get() = sizeScaler.toDesign(this)
    val Float.toActual   get() = sizeScaler.toActual(this)
    val Float.toDesign   get() = sizeScaler.toDesign(this)

    private val onceInit = AtomicBoolean(true)

    private val mapIsTransform = mutableMapOf<AdvancedGroup, Boolean>()


    abstract fun addActorsOnGroup()

    override fun draw(batch: Batch?, parentAlpha: Float) {
        preDrawArray.forEach { it.draw(parentAlpha * this@AdvancedGroup.color.a) }
        super.draw(batch, parentAlpha)
        postDrawArray.forEach { it.draw(parentAlpha * this@AdvancedGroup.color.a) }
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)

        tryInitGroup()
        // Якщо розмірів або stage немає, виконаємо це в `sizeChanged()`
    }

    override fun sizeChanged() {
        super.sizeChanged()

        tryInitGroup()
        // Якщо розмірів або stage немає, виконаємо це в `setStage()`
    }

    private fun tryInitGroup() {
        // Якщо є розміри і stage, то ініціалізацію виконуємо один раз.
        // (Це абсолютно моє рішення, якщо ви знаєте краще використовуйте його)
        if (width > 0 && height > 0 && stage != null) {
            sizeScaler.calculateScale(Vector2(width, height))
            if (onceInit.getAndSet(false)) { addActorsOnGroup() }
        } else {
            //log("[no tryInitGroup] $currentClassName: {w = $width | h = $height | stage = ${stage != null}}")
        }
    }

    override fun dispose() {
        if (isDisposed.not()) {
            preDrawArray.clear()
            postDrawArray.clear()

            disposableSet.disposeAll()
            disposableSet.clear()

            disposeAndClearChildren()

            cancelCoroutinesAll(coroutine)
            coroutine = null

            isDisposed = true
        }
    }

    override fun remove(): Boolean {
        if (isDisposeOnRemove) dispose()
        return super.remove()
    }

    fun disposeAndClearChildren() {
        children.onEach { actor -> if (actor is Disposable) actor.dispose() }
        clearChildren()
    }

    private fun setIsTransformAll(newIsTransform: Boolean, states: MutableMap<AdvancedGroup, Boolean>) {
        states[this] = isTransform
        isTransform  = newIsTransform

        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]
            if(child is AdvancedGroup) child.setIsTransformAll(newIsTransform, states)
        }
        children.end()
    }

    private fun restoreTransforms(states: Map<AdvancedGroup, Boolean>) {
        for ((group, state) in states) {
            group.isTransform = state
        }
        mapIsTransform.clear()
    }

    fun drawChildrenWithoutTransform(batch: Batch, parentAlpha: Float) {
        // Вимикаємо трансформації у всьому дереві
        setIsTransformAll(false, mapIsTransform)

        // Малюємо усіх дітей (усі групи без трансформацій)
        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]

            if (child.isVisible) {
                child.draw(batch, parentAlpha /* * child.color.a*/)
            }

        }
        children.end()

        // Відновлюємо трансформації після малювання
        restoreTransforms(mapIsTransform)
    }

    protected fun Actor.setBoundsScaled(x: Float, y: Float, width: Float, height: Float) {
        setBounds(x.toActual, y.toActual, width.toActual, height.toActual)
    }

    protected fun Actor.setBoundsScaled(position: Vector2, size: Vector2) {
        setBoundsScaled(position.x, position.y, size.x, size.y)
    }

    protected fun Actor.setSizeScaled(width: Float, height: Float) {
        setSize(width.toActual, height.toActual)
    }

    fun interface Drawer {
        fun draw(alpha: Float)
    }

}