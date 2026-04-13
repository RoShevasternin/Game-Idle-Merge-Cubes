package com.lewydo.idlemergecubes.game.utils.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

/**
 * Raw GL blit primitive — аналог Unity's Graphics.Blit(src, dst, material).
 *
 * ─── ЧОМУ НЕ SpriteBatch ────────────────────────────────────────────────────
 * SpriteBatch будує геометрію (4 вершини, матриці, vertex buffer upload) і
 * потребує begin()/end() навколо кожної операції. Для FBO-to-FBO операцій
 * (blur pass, mask pass, color correction) це зайвий overhead.
 *
 * Blit малює повноекранний quad через raw GL:
 *   • 4 вершини в NDC (-1..1), без будь-яких матриць
 *   • mesh compile once, reuse forever
 *   • GL state changes: тільки shader bind + texture bind
 *   • не потребує batch.begin()/end()
 *
 * ─── ВИКОРИСТАННЯ ──────────────────────────────────────────────────────────
 * ```kotlin
 * // Copy src → dst (passthrough):
 * Blit.blit(src, dst)
 *
 * // Apply blur shader:
 * Blit.blit(src, dst, blurShader) { shader ->
 *     shader.setUniformf("u_direction", 1f, 0f)
 *     shader.setUniformf("u_blurAmount", radius)
 * }
 *
 * // Passthrough shader vertex source — use in your custom shaders:
 * // val myShader = ShaderProgram(Blit.VERT, myFragmentSource)
 * ```
 *
 * ─── ПРИМІТКА ПРО UV ──────────────────────────────────────────────────────
 * Mesh UV: (0,0) = bottom-left, (1,1) = top-right.
 * FBO зберігає пікселі з Y-axis up (OpenGL convention).
 * Для FBO-to-FBO blit ніякого flip не потрібно — обидва буфери в одному space.
 * Flip (false, true) застосовується тільки при фінальному draw на екран
 * через SpriteBatch (в EffectGroup.draw).
 */
object Blit : Disposable {

    /** -------------------------------------------------------------------------
    // Vertex shader для всіх Blit операцій.
    //
    // NDC space (-1..1) — gl_Position = a_position без будь-яких матриць.
    // Це сумісно з fragment shaders що використовують v_texCoords,
    // включаючи gaussianBlurFS.glsl і hslColorFS.glsl.
    // ------------------------------------------------------------------------- */
    const val VERT = """
        attribute vec4 a_position;
        attribute vec2 a_texCoord0;
        varying vec2 v_texCoords;
        void main() {
            v_texCoords = a_texCoord0;
            gl_Position = a_position;
        }
    """

    /** Passthrough shader — просто копіює текстуру без змін */
    val passthroughShader: ShaderProgram by lazy {
        ShaderProgram(
            VERT,
            """
            #ifdef GL_ES
            precision mediump float;
            #endif
            varying vec2 v_texCoords;
            uniform sampler2D u_texture;
            void main() {
                gl_FragColor = texture2D(u_texture, v_texCoords);
            }
            """.trimIndent()
        ).also {
            if (!it.isCompiled) Gdx.app.error("Blit", "Passthrough shader error:\n${it.log}")
        }
    }

    /** -------------------------------------------------------------------------
    // Full-screen quad mesh в NDC координатах.
    //
    // Compile once при першому використанні, залишається в пам'яті назавжди.
    // Mesh не має ніяких transforms — він просто покриває весь clip space.
    // ------------------------------------------------------------------------- */
    private val mesh: Mesh by lazy {
        Mesh(
            true, 4, 6,
            VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
            VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "${ShaderProgram.TEXCOORD_ATTRIBUTE}0")
        ).apply {
            //        x     y    u    v
            setVertices(floatArrayOf(
                -1f, -1f,  0f,  0f,   // bottom-left
                1f, -1f,  1f,  0f,   // bottom-right
                1f,  1f,  1f,  1f,   // top-right
                -1f,  1f,  0f,  1f,   // top-left
            ))
            setIndices(shortArrayOf(0, 1, 2, 2, 3, 0))
        }
    }

    /** -------------------------------------------------------------------------
    // Основний метод — blit src → dst з шейдером.
    //
    // @param src      вхідний FrameBuffer (джерело)
    // @param dst      цільовий FrameBuffer (null = поточний render target)
    // @param shader   шейдер для обробки (за замовчуванням passthrough)
    // @param uniforms лямбда для передачі uniforms в шейдер
    //
    // Після виклику:
    //   • якщо dst != null → dst.end() вже викликаний, viewport відновлений
    //   • blend відновлений в GL_SRC_ALPHA / GL_ONE_MINUS_SRC_ALPHA
    //   • SpriteBatch стан не зачеплений (Blit не знає про batch)
    // ------------------------------------------------------------------------- */
    fun blit(
        src     : FrameBuffer,
        dst     : FrameBuffer?,
        shader  : ShaderProgram = passthroughShader,
        uniforms: (ShaderProgram) -> Unit = {}
    ) {
        // Активуємо цільовий буфер і очищаємо його
        dst?.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Premultiplied alpha — стандартний blend для FBO контенту
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFuncSeparate(
            GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA,
            GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA
        )

        // Bind шейдер, текстуру, uniforms
        shader.bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        src.colorBufferTexture.bind(0)
        shader.setUniformi("u_texture", 0)
        uniforms(shader)

        // Рендеримо quad — 6 індексів = 2 трикутники = повний екран FBO
        mesh.render(shader, GL20.GL_TRIANGLES)

        // Відновлюємо стандартний blend для SpriteBatch
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        // Деактивуємо цільовий буфер (відновлює viewport до розміру екрану)
        dst?.end()
    }

    override fun dispose() {
        if (::passthroughShader.isInitialized && passthroughShader.isCompiled) passthroughShader.dispose()
        if (mesh.isManaged) mesh.dispose()
    }

}