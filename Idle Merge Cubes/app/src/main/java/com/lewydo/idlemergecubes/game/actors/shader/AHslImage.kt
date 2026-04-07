package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.createShader
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlin.math.abs

/**
 * Перефарбовує будь-який вміст через HSL-шейдер, зберігаючи luminance оригіналу.
 *
 * ─── Яку текстуру давати ───────────────────────────────────────────────────
 * Рекомендується GRAYSCALE спрайт (saturation = 0 в Figma):
 *   • один спрайт покриває будь-який колір
 *   • результат чистий і передбачуваний
 *   • бліки = світлі пікселі → залишаються світлими незалежно від hue
 *   • тіні = темні пікселі  → залишаються темними
 *
 * Кольорова текстура теж працює — оригінальний hue ігнорується,
 * береться тільки luminance. Але grayscale чистіше.
 *
 * ─── Використання ──────────────────────────────────────────────────────────
 * ```kotlin
 * val cube = AHslImage(screen, region)
 * cube.setSize(200f, 200f)
 *
 * // Через hex (найзручніше):
 * cube.setColor("A855F7")          // фіолетовий
 * cube.setColor("22D3EE")          // блакитний
 * cube.setColor("D9D9D9")          // нейтральний сірий (майже без кольору)
 *
 * // Через HSL вручну:
 * cube.setHsl(AHslImage.Hue.PURPLE)
 * cube.setHsl(0.75f, saturation = 1f, luminance = 0.05f)
 *
 * // Змінити drawable без пересоздання:
 * cube.drawable = TextureRegionDrawable(otherRegion)
 *
 * // Заморозити якщо колір статичний (не перерендерює FBO кожен кадр):
 * cube.isStaticEffect = true
 * ```
 */
class AHslImage(
    override val screen: AdvancedScreen,
    private val initialDrawable: Drawable? = null,
) : PreRenderableGroup() {

    override val useFboGroup = false

    companion object {
        /**
         * Shared шейдер — один на весь клас.
         * НЕ dispose-уємо в instance.dispose() — тільки при виході з гри.
         */
        val shader: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/hslColor/hslColorFS.glsl"
            )
        }

        // ─── RGB → HSL конвертація ────────────────────────────────────────
        // Використовується для setColor(hex) — конвертуємо hex → RGB → HSL
        // щоб передати в шейдер як h, s, l.

        private fun rgbToHsl(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
            val maxC = maxOf(r, g, b)
            val minC = minOf(r, g, b)
            val l    = (maxC + minC) / 2f
            val d    = maxC - minC

            if (d < 0.001f) return Triple(0f, 0f, l) // achromatic (сірий)

            val s = d / (1f - abs(2f * l - 1f))

            val h = when (maxC) {
                r    -> (((g - b) / d) % 6f) / 6f
                g    -> ((b - r) / d + 2f) / 6f
                else -> ((r - g) / d + 4f) / 6f
            }.let { if (it < 0f) it + 1f else it }

            return Triple(h, s, l)
        }
    }

    // ─── HSL параметри ───────────────────────────────────────────────────────

    /** Відтінок: 0.0 – 1.0 */
    var hue        : Float = 0f     ; private set

    /** Насиченість: 0.0 (сірий) – 1.0 (повна) */
    var saturation : Float = 1f  ; private set

    /** Зміщення яскравості: -1.0 – 1.0 (0 = зберегти оригінал) */
    var luminance  : Float = 0f     ; private set

    // ─── Image ───────────────────────────────────────────────────────────────

    private val image = Image()

    /** Змінює drawable без пересоздання актора */
    var drawable: Drawable?
        get()      = image.drawable
        set(value) { image.drawable = value }

    // ─── Constructors ─────────────────────────────────────────────────────────

    constructor(screen: AdvancedScreen, region : TextureRegion) : this(screen, TextureRegionDrawable(region))
    constructor(screen: AdvancedScreen, texture: Texture      ) : this(screen, TextureRegionDrawable(texture))
    constructor(screen: AdvancedScreen, patch  : NinePatch    ) : this(screen, NinePatchDrawable(patch))

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        if (initialDrawable != null) {
            image.drawable = initialDrawable
            addAndFillActor(image)
        }
        createFrameBuffer()
    }

    // ─── PreRenderMethods ─────────────────────────────────────────────────────

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
        }

        override fun applyEffect(batch: Batch, parentAlpha: Float) = Unit

        /**
         * Малюємо fboGroup → fboResult з HSL-шейдером.
         * Шейдер замінює hue/saturation, luminance пікселя оригіналу зберігається.
         */
        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            batch.shader = shader

            shader.setUniformf("u_hue",        hue)
            shader.setUniformf("u_saturation", saturation)
            shader.setUniformf("u_luminance",  luminance)

            //batch.draw(textureGroup, 0f, 0f, width, height)
            drawChildrenWithoutTransform(batch, parentAlpha)

            batch.shader = null
        }
    }

    // ─── API ──────────────────────────────────────────────────────────────────

    /**
     * Встановлює колір через HEX-рядок.
     *
     * Шейдер витягне hue і saturation із зазначеного кольору,
     * а luminance кожного пікселя текстури збереже.
     *
     * @param hex       Hex-колір: "D9D9D9", "#A855F7", "22d3ee" тощо.
     * @param luminance Зміщення яскравості -1.0–1.0. 0 = зберегти оригінал.
     *
     * Приклади:
     * ```kotlin
     * cube.setColor("A855F7")          // фіолетовий
     * cube.setColor("22D3EE")          // блакитний
     * cube.setColor("EAB308")          // жовтий
     * cube.setColor("D9D9D9")          // нейтральний (майже сірий, мала sat)
     * cube.setColor("FF0000", -0.1f)   // червоний, трохи темніше
     * ```
     */
    fun setColorShader(hex: String, luminance: Float = 0f) {
        val color = Color.valueOf(hex)
        val (h, s, _) = rgbToHsl(color.r, color.g, color.b)
        this.hue        = h
        this.saturation = s
        this.luminance  = luminance
    }

    fun setColorShader(color: Color, luminance: Float = 0f) {
        val (h, s, _) = rgbToHsl(color.r, color.g, color.b)
        this.hue        = h
        this.saturation = s
        this.luminance  = luminance
    }

}