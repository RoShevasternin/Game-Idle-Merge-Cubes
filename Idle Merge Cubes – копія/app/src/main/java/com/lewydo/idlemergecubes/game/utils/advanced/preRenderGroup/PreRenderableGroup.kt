package com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.ScreenUtils
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.disposeAll

abstract class PreRenderableGroup: AdvancedGroup(), PreRenderable {

    protected var fboGroup  : FrameBuffer? = null
    protected var fboResult : FrameBuffer? = null

    var textureGroup : TextureRegion? = null
        private set
    var textureResult: TextureRegion? = null
        private set

    protected val identityMatrix: Matrix4 = Matrix4().idt()

    protected var camera = OrthographicCamera()

    private val preRenderMethod: PreRenderMethods = getPreRenderMethods()

    // --- Static effect ---
    private var staticEffectRenderCounter = 0
    var isStaticEffect = false
        set(value) {
            if (!value) staticEffectRenderCounter = 0
            field = value
        }

    /** -------------------------------------------------------------------------
    // Скільки кадрів рендерити після встановлення isStaticEffect = true
    // перед тим як "заморозити" результат (мінімум 2, щоб FBO встиг заповнитись)
    // ------------------------------------------------------------------------- */
    var staticEffectFrames = 2

    override fun addActorsOnGroup() {
        createFrameBuffer()
    }

    override fun dispose() {
        super.dispose()
        disposeAll(fboGroup, fboResult)
    }

    abstract fun getPreRenderMethods(): PreRenderMethods

    /** -------------------------------------------------------------------------
    // draw() — малює textureResult на екран з правильною alpha
    //
    // Використовуємо premultiplied alpha blend (GL_ONE, GL_ONE_MINUS_SRC_ALPHA),
    // тому RGB теж множиться на alpha — інакше буде "темна окантовка" на краях.
    // ------------------------------------------------------------------------- */

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null) throw Exception("Error draw: ${this::class.simpleName}")
        if (textureResult == null) return

        val a = color.a * parentAlpha

        // ABlur = { batch.setColor(color.r, color.g, color.b, 1f) }

        // Premultiplied alpha: множимо RGB на alpha
        batch.setColor(color.r * a, color.g * a, color.b * a, a)

        batch.end()
        batch.begin()

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.draw(
            textureResult,
            x, y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation,
        )

        batch.end()
        batch.begin()

        // Відновлюємо стандартний blend і колір
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.setColor(Color.WHITE)
    }

    /** -------------------------------------------------------------------------
    // preRender() — рендерить вміст у FBO до основного draw()
    //
    // Alpha в FBO ЗАВЖДИ = 1.0 (рендеримо в повній якості).
    // parentAlpha застосовується тільки у фінальному draw() вище.
    // -------------------------------------------------------------------------- */

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (logicIsStaticEffect()) return

        if (fboGroup == null || fboResult == null) throw Exception("Error preRender: ${this::class.simpleName}")

        // Зберігаємо стан batch ДО всього pipeline —
        // щоб preRender не мав side effects для інших акторів на тому ж рівні.
        val savedColor = batch.color.cpy()

        batch.end()

        // 1. Рекурсивно викликаємо preRender у дочірніх PreRenderableGroup.
        //    Передаємо 1f — кожна дочірня група рендерить свій FBO в повній якості.
        //    Власний color.a кожної дитини вона застосує сама у своєму draw().
        batch.begin()
        preRenderChildren(batch, 1f)
        batch.end()

        // 2. Рендеримо вміст групи в fboGroup — теж 1f:
        //    FBO завжди заповнюється в повній якості, parentAlpha не передається вглиб.
        fboGroup!!.beginAdvanced(batch)
        batch.setBlendFunctionSeparate(
            GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
            GL20.GL_ONE,       GL20.GL_ONE_MINUS_SRC_ALPHA
        )
        batch.withMatrix(camera.combined, identityMatrix) {
            preRenderMethod.renderFboGroup(batch, 1f)
        }
        fboGroup!!.endAdvanced(batch)

        // 3. Застосовуємо ефект (blur, mask тощо) — проміжні FBO, теж 1f
        preRenderMethod.applyEffect(batch, 1f)

        // 4. Рендеримо фінальний результат в fboResult — теж 1f
        fboResult!!.beginAdvanced(batch)
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.withMatrix(camera.combined, identityMatrix) {
            preRenderMethod.renderFboResult(batch, 1f)
        }
        fboResult!!.endAdvanced(batch)

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.color = savedColor
        batch.begin()
    }

    /** -------------------------------------------------------------------------
    // Обхід дочірніх PreRenderableGroup
    //
    // Передаємо 1f — FBO кожної дочірньої групи рендериться в повній якості.
    // Реальна alpha (своя + батьківська) застосовується пізніше в draw()
    // кожної групи окремо, коли вона виводить textureResult на екран.
    // ------------------------------------------------------------------------- */

    private fun preRenderChildren(batch: Batch, parentAlpha: Float) {
        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]
            renderPreRenderables(child, batch, parentAlpha) //stage.root.color.a (ABlur)
        }
        children.end()
    }

    /** -------------------------------------------------------------------------
    // Frame Buffer creation
    // ------------------------------------------------------------------------- */

    protected open fun createFrameBuffer() {
        camera = OrthographicCamera(width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        fboGroup  = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
        fboResult = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)

        textureGroup  = TextureRegion(fboGroup!!.colorBufferTexture ).apply { flip(false, true) }
        textureResult = TextureRegion(fboResult!!.colorBufferTexture).apply { flip(false, true) }
    }

    /** -------------------------------------------------------------------------
    // Static effect logic
    //
    // isStaticEffect = true → рендеримо staticEffectFrames кадрів, потім зупиняємо.
    // Це дозволяє "заморозити" результат і не витрачати GPU кожен кадр.
    // ------------------------------------------------------------------------- */

    private fun logicIsStaticEffect(): Boolean {
        if (!isStaticEffect) return false

        staticEffectRenderCounter++
        return staticEffectRenderCounter > staticEffectFrames
    }

    /** -------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------- */

    protected inline fun Batch.withMatrix(
        newProjectionMatrix: Matrix4,
        newTransformMatrix: Matrix4,
        block: Block
    ) {
        val oldProj  = projectionMatrix
        val oldTrans = transformMatrix
        projectionMatrix = newProjectionMatrix
        transformMatrix  = newTransformMatrix
        block()
        projectionMatrix = oldProj
        transformMatrix  = oldTrans
    }

    /** -------------------------------------------------------------------------
    // Починає рендер в FBO: активує буфер, очищає його, стартує batch.
    // ------------------------------------------------------------------------- */
    protected fun FrameBuffer.beginAdvanced(batch: Batch) {
        begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.color = Color.WHITE
        batch.begin()
    }

    /** -------------------------------------------------------------------------
    // Завершує рендер в FBO: зупиняє batch, деактивує буфер,
    // відновлює viewport і матриці до стану stage.
    // Зберігаємо і відновлюємо batch.color, щоб не руйнувати
    // alpha-контекст батьківського pipeline.
    // ------------------------------------------------------------------------- */

    protected fun FrameBuffer.endAdvanced(batch: Batch) {
        batch.end()
        end()

        stage.viewport.apply()
        batch.projectionMatrix = stage.camera.combined
        batch.transformMatrix  = identityMatrix

        batch.shader = null

        //batch.color = Color.WHITE
    }

}