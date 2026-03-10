package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
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

open class ABlur(
    override val screen: AdvancedScreen,
    var textureRegionBlur: TextureRegion? = null,
): PreRenderableGroup() {

    companion object {
        private val shaderProgram: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/blur/gaussianBlurFS.glsl"
            )
        }
    }

    private var shaderProgram: ShaderProgram? = null

    private var fboBlurH    : FrameBuffer?   = null
    private var fboBlurV    : FrameBuffer?   = null

    private var textureBlurV : TextureRegion? = null
    private var textureBlurH : TextureRegion? = null

    var isBlurEnabled = false
        private set

    var radiusBlur = 0f
        set(value) {
            isBlurEnabled = (value != 0f)
            field = value
        }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        createFrameBuffer()
    }

    override fun dispose() {
        super.dispose()
        disposeAll(shaderProgram, fboBlurH, fboBlurV)
    }

    // -------------------------------------------------------------------------
    // PreRenderMethods
    // -------------------------------------------------------------------------

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            if (textureRegionBlur != null) {
                //batch.setColor(color.r, color.g, color.b, parentAlpha)
                //batch.color = Color.WHITE
                batch.draw(textureRegionBlur, 0f, 0f, width, height)
            } else {
                drawChildrenWithoutTransform(batch, parentAlpha)
            }
        }

        override fun applyEffect(batch: Batch, parentAlpha: Float) {
            if (isBlurEnabled.not()) return

            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

            batch.applyBlur(fboBlurH, textureGroup, 1f, 0f)
            batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)

            batch.applyBlur(fboBlurH, textureBlurV, 0.707f, 0.707f)
            batch.applyBlur(fboBlurV, textureBlurH, -0.707f, -0.707f)

            batch.applyBlur(fboBlurH, textureBlurV, 0.383f, 0.924f)
            batch.applyBlur(fboBlurV, textureBlurH, 0.924f, 0.383f)

            //batch.applyBlur(fboBlurH, textureBlurV, 1f, 0f)
            //batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)
        }

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            val src = if (isBlurEnabled) textureBlurV else textureGroup
            batch.draw(src, 0f, 0f, width, height)
        }
    }

    /** -------------------------------------------------------------------------
    // preRender override — перевіряємо що shaders/FBO готові
    // ------------------------------------------------------------------------- */

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (shaderProgram == null || fboBlurH == null || fboBlurV == null)
            throw Exception("Error preRender: ${this::class.simpleName}")

        super.preRender(batch, parentAlpha)
    }

    /** -------------------------------------------------------------------------
    // Frame Buffer
    // ------------------------------------------------------------------------- */

    override fun createFrameBuffer() {
        super.createFrameBuffer()

        fboBlurH = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
        fboBlurV = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)

        textureBlurH = TextureRegion(fboBlurH!!.colorBufferTexture).apply { flip(false, true) }
        textureBlurV = TextureRegion(fboBlurV!!.colorBufferTexture).apply { flip(false, true) }
    }

    /** -------------------------------------------------------------------------
    // applyBlur helper
    //
    // Виконує один blur-пас: рендер текстури в FBO з шейдером по осі (dH, dV).
    // Шейдер встановлюється і скидається всередині — не впливає на зовнішній стан batch.
    // batch.color не чіпаємо — blur працює з яскравістю пікселів напряму через uniform.
    // ------------------------------------------------------------------------- */

    private fun Batch.applyBlur(
        fbo          : FrameBuffer?,
        textureRegion: TextureRegion?,
        dH: Float,
        dV: Float
    ) {
        requireNotNull(fbo)           { "applyBlur: fbo is null"           }
        requireNotNull(textureRegion) { "applyBlur: textureRegion is null" }

        fbo.beginAdvanced(this)

        shader = shaderProgram
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureRegion.texture.bind(0)

        shaderProgram!!.setUniformi("u_texture",    0)
        shaderProgram!!.setUniformf("u_groupSize",  fbo.width.toFloat(), fbo.height.toFloat())
        shaderProgram!!.setUniformf("u_blurAmount", radiusBlur)
        shaderProgram!!.setUniformf("u_direction",  dH, dV)

        withMatrix(camera.combined, identityMatrix) {
            draw(textureRegion, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
        }

        fbo.endAdvanced(this)
    }

}