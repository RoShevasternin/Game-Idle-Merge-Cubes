package com.lewydo.idlemergecubes.game.actors.vfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.createShader
import com.lewydo.idlemergecubes.game.utils.disposeAll

open class ABlurOLD(
    override val screen: AdvancedScreen,
    var textureRegionBlur: TextureRegion? = null,
) : PreRenderableGroup() {

    companion object {
        private val shaderProgram: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/blur/gaussianBlurFS.glsl"
            )
        }
    }

    private var fboBlurH : FrameBuffer? = null
    private var fboBlurV : FrameBuffer? = null

    private var textureBlurH: TextureRegion? = null
    private var textureBlurV: TextureRegion? = null

    var isBlurEnabled = false
        private set

    var radiusBlur = 0f
        set(value) {
            isBlurEnabled = (value != 0f)
            field = value
        }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        createFrameBuffer()
    }

    override fun dispose() {
        super.dispose()
        disposeAll(fboBlurH, fboBlurV)
    }

    // ── PreRenderMethods ──────────────────────────────────────────────────────

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            if (textureRegionBlur != null) {
                batch.draw(textureRegionBlur, 0f, 0f, width, height)
            } else {
                drawChildrenWithoutTransform(batch, parentAlpha)
            }
        }

        override fun applyEffect(batch: Batch, parentAlpha: Float) {
            if (!isBlurEnabled) return

            // Всі blur passes використовують premultiplied alpha —
            // вміст fboGroup вже premultiplied після preRender
            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

            // 4 passes: H, V, 45°, 135° — для круглого розмиття
            applyBlurPass(batch, fboBlurH, textureGroup,  1f,      0f     )
            applyBlurPass(batch, fboBlurV, textureBlurH,  0f,      1f     )
            applyBlurPass(batch, fboBlurH, textureBlurV,  0.383f,  0.924f )
            applyBlurPass(batch, fboBlurV, textureBlurH,  0.924f,  0.383f )
        }

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            val src = if (isBlurEnabled) textureBlurV else textureGroup
            batch.draw(src, 0f, 0f, width, height)
        }
    }

    // ── FBO ───────────────────────────────────────────────────────────────────

    override fun createFrameBuffer() {
        super.createFrameBuffer()

        val w = width.toInt().coerceAtLeast(1)
        val h = height.toInt().coerceAtLeast(1)

        fboBlurH = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)
        fboBlurV = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)

        textureBlurH = TextureRegion(fboBlurH!!.colorBufferTexture).apply { flip(false, true) }
        textureBlurV = TextureRegion(fboBlurV!!.colorBufferTexture).apply { flip(false, true) }
    }

    // ── Blur pass ─────────────────────────────────────────────────────────────

    private fun applyBlurPass(
        batch         : Batch,
        fbo           : FrameBuffer?,
        textureRegion : TextureRegion?,
        dH: Float,
        dV: Float
    ) {
        requireNotNull(fbo)           { "applyBlurPass: fbo is null"           }
        requireNotNull(textureRegion) { "applyBlurPass: textureRegion is null" }

        // Використовуємо новий beginFbo/endFbo з PreRenderableGroup
        fbo.beginFbo(batch)

        batch.shader = shaderProgram
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureRegion.texture.bind(0)

        shaderProgram.setUniformi("u_texture",    0)
        shaderProgram.setUniformf("u_groupSize",  fbo.width.toFloat(), fbo.height.toFloat())
        shaderProgram.setUniformf("u_blurAmount", radiusBlur)
        shaderProgram.setUniformf("u_direction",  dH, dV)

        batch.withMatrix(camera.combined, identityMatrix) {
            batch.draw(textureRegion, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
        }

        batch.shader = null
        fbo.endFbo(batch)
    }
}