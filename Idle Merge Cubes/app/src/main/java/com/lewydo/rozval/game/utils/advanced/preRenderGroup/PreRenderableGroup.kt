package com.lewydo.rozval.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.ScreenUtils
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.disposeAll

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

    private var staticEffectRenderCounter = 0
    var isStaticEffect = false

    override fun addActorsOnGroup() {
        createFrameBuffer()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null) throw Exception("Error draw: ${this::class.simpleName}")

        batch.setColor(color.r, color.g, color.b, 1f)

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
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun dispose() {
        super.dispose()
        disposeAll(fboGroup, fboResult)
    }

    abstract fun getPreRenderMethods(): PreRenderMethods

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (logicIsStaticEffect()) return

        if (fboGroup == null || fboResult == null) throw Exception("Error preRender: ${this::class.simpleName}")

        batch.end()

        // Викликаємо PreRender спочатку у дітей
        batch.begin()
        preRenderChildren(batch)
        batch.end()

        /** RENDER fboGroup - Рендеримо в fboGroup */
        fboGroup!!.beginAdvanced(batch)
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            preRenderMethod.renderFboGroup(batch, parentAlpha)
        }
        fboGroup!!.endAdvanced(batch)

        /** RENDER FBO - Рендеримо в інші FrameBuffer */
        preRenderMethod.applyEffect(batch, parentAlpha)

        /** RENDER fboResult - Рендеримо в fboResult */
        fboResult!!.beginAdvanced(batch)
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            preRenderMethod.renderFboResult(batch, parentAlpha)
        }
        fboResult!!.endAdvanced(batch)

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
    }

    private fun preRenderChildren(batch: Batch) {
        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]
            renderPreRenderables(child, batch, stage.root.color.a)
        }
        children.end()
    }

    protected open fun createFrameBuffer() {
        camera = OrthographicCamera(width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        fboGroup  = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
        fboResult = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)

        textureGroup  = TextureRegion(fboGroup!!.colorBufferTexture)
        textureResult = TextureRegion(fboResult!!.colorBufferTexture)

        textureGroup!!.flip(false, true)
        textureResult!!.flip(false, true)
    }

    private fun logicIsStaticEffect(): Boolean {
        return if (isStaticEffect) {
            staticEffectRenderCounter++
            staticEffectRenderCounter >= 5
        } else {
            staticEffectRenderCounter = 0
            false
        }
    }

    protected inline fun Batch.withMatrix(newProjectionMatrix: Matrix4, newTransformMatrix: Matrix4, block: () -> Unit) {
        val oldProj  = projectionMatrix
        val oldTrans = transformMatrix
        projectionMatrix = newProjectionMatrix
        transformMatrix  = newTransformMatrix
        block()
        projectionMatrix = oldProj
        transformMatrix  = oldTrans
    }

    protected fun FrameBuffer.beginAdvanced(batch: Batch) {
        begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.begin()
    }

    protected fun FrameBuffer.endAdvanced(batch: Batch) {
        batch.end()
        end()

        stage.viewport.apply()

        batch.projectionMatrix = stage.camera.combined
        batch.transformMatrix  = identityMatrix

        batch.color = Color.WHITE
        batch.shader = null
    }

}