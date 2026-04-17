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

/**
 * PreRenderableGroup — базова група для FBO-шейдерів.
 *
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  АРХІТЕКТУРА — двопрохідний рендеринг                            ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  Прохід 1 — preRender() (ПЕРЕД stage.draw)                       ║
 * ║    ↓ renderFboGroup   → fboGroup  (вміст групи)                  ║
 * ║    ↓ applyEffect      → проміжні FBO (blur, тощо)                ║
 * ║    ↓ renderFboResult  → fboResult (фінал з шейдером)             ║
 * ║                                                                  ║
 * ║  Прохід 2 — draw() (В stage.draw)                                ║
 * ║    ↓ малює textureResult на екран з parentAlpha                  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  BLEND СТРАТЕГІЯ                                                 ║
 * ║                                                                  ║
 * ║  В FBO: setBlendFunctionSeparate(                                ║
 * ║    GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA,  ← RGB: звичайний       ║
 * ║    GL_ONE,       GL_ONE_MINUS_SRC_ALPHA   ← A: правильне накоп.  ║
 * ║  )                                                               ║
 * ║  Результат FBO — premultiplied alpha                             ║
 * ║                                                                  ║
 * ║  draw(): GL_ONE, GL_ONE_MINUS_SRC_ALPHA  (premul compositing)    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ПРАВИЛА для підкласів                                           ║
 * ║  • renderFboGroup  — малює вміст (drawChildrenWithoutTransform)  ║
 * ║  • applyEffect     — проміжні FBO (необов'язково)                ║
 * ║  • renderFboResult — фінал з шейдером, малює textureGroup        ║
 * ║  • shader скидається автоматично після renderFboResult           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
abstract class PreRenderableGroup : AdvancedGroup(), PreRenderable {

    // ── FBO ──────────────────────────────────────────────────────────────────

    protected var fboGroup : FrameBuffer? = null
    protected var fboResult: FrameBuffer? = null

    var textureGroup : TextureRegion? = null
        protected set
    var textureResult: TextureRegion? = null
        protected set

    protected open val useFboGroup: Boolean = true

    // ── Camera і матриці ──────────────────────────────────────────────────────

    protected var camera = OrthographicCamera()
    protected val identityMatrix = Matrix4().idt()

    // Pre-allocated — без GC в render loop
    private val savedColor      = Color()
    private val savedProjMatrix = Matrix4()
    private var savedBlendSrc   = GL20.GL_SRC_ALPHA
    private var savedBlendDst   = GL20.GL_ONE_MINUS_SRC_ALPHA

    protected val tmpProjMatrix  = Matrix4()
    protected val tmpTransMatrix = Matrix4()

    // ── Static Effect ──────────────────────────────────────────────────────────

    private var staticCounter = 0

    /**
     * Якщо true — рендерить ще [staticEffectFrames] кадрів, потім заморожує FBO.
     * При встановленні false — скидає лічильник (дозволяє оновлення).
     */
    var isStaticEffect = false
        set(value) {
            field = value
            if (!value) staticCounter = 0
        }

    /**
     * Скільки кадрів рендерити після isStaticEffect=true перед заморозкою.
     * Мінімум 2 — щоб FBO встиг заповнитись коректно.
     */
    var staticEffectFrames = 2

    /**
     * Примусово рендерить один кадр навіть якщо isStaticEffect=true.
     * Корисно при зміні uniform параметрів шейдера.
     */
    fun rerenderStaticOnce() {
        staticCounter = 0
    }

    // ── PreRenderMethods ───────────────────────────────────────────────────────

    private val methods: PreRenderMethods = getPreRenderMethods()

    abstract fun getPreRenderMethods(): PreRenderMethods

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        createFrameBuffer()
    }

    /**
     * Перестворюємо FBO якщо розмір змінився ПІСЛЯ ініціалізації.
     * Без цього шейдер рендерить у буфер старого розміру.
     */
    override fun sizeChanged() {
        super.sizeChanged()
        // fboGroup != null означає що вже ініціалізовано
        if (fboGroup != null && (
            width.toInt() != fboGroup!!.width ||
            height.toInt() != fboGroup!!.height
        )) {
            recreateFrameBuffer()
        }
    }

    override fun dispose() {
        super.dispose()
        disposeAll(fboGroup, fboResult)
    }

    // ── draw() ─────────────────────────────────────────────────────────────────
    //
    // Малює textureResult на екран з правильним premultiplied alpha.
    //
    // ВАЖЛИВО: зберігаємо і відновлюємо blend — draw() може викликатись
    // зсередини FBO батька (в drawChildrenWithoutTransform), де батько
    // вже встановив правильний blend для FBO рендерингу.
    // Жорстке скидання до GL_SRC_ALPHA ламало б blend батька.

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch ?: return
        val result = textureResult ?: return

        val a = color.a * parentAlpha

        // Зберігаємо поточний blend батька
        val prevSrc = batch.blendSrcFunc
        val prevDst = batch.blendDstFunc

        // Premultiplied alpha: RGB вже помножені на alpha в FBO
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.setColor(color.r * a, color.g * a, color.b * a, a)

        batch.draw(
            result,
            x, y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation,
        )

        // Відновлюємо blend батька, а не "стандарт" — бо ми можемо бути в FBO
        batch.setBlendFunction(prevSrc, prevDst)
        batch.setColor(Color.WHITE)
    }

    // ── preRender() ────────────────────────────────────────────────────────────
    //
    // Викликається з AdvancedStage ПЕРЕД stage.draw().
    // Заповнює fboResult готовою текстурою.

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (shouldSkipRender()) return

        val fboR = fboResult ?: return

        // ── Зберігаємо стан batch ────────────────────────────────────────────
        // Pre-allocated поля — без GC
        savedColor.set(batch.color)
        savedProjMatrix.set(batch.projectionMatrix)
        savedBlendSrc = batch.blendSrcFunc
        savedBlendDst = batch.blendDstFunc

        // Зупиняємо batch якщо він активний
        if (batch.isDrawing) batch.end()

        // ── Крок 1: рекурсивно preRender вкладених PreRenderableGroup ─────────
        // Вони мають заповнити свої FBO ДО того як ми відкриємо наш
        batch.begin()
        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]
            if (child.isVisible) renderPreRenderables(child, batch, 1f)
        }
        children.end()
        if (batch.isDrawing) batch.end()

        // ── Крок 2: рендер вмісту в fboGroup ─────────────────────────────────

        if (useFboGroup) {
            val fboG = fboGroup ?: return

            fboG.begin()
            ScreenUtils.clear(Color.CLEAR, true)
            batch.projectionMatrix = camera.combined
            batch.transformMatrix  = identityMatrix
            batch.begin()
            // Правильний blend для FBO:
            // RGB — стандартний alpha blend
            // A   — накопичується правильно (GL_ONE = зберегти src alpha)
            batch.setBlendFunctionSeparate(
                GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                GL20.GL_ONE,       GL20.GL_ONE_MINUS_SRC_ALPHA
            )
            batch.color = Color.WHITE
            methods.renderFboGroup(batch, 1f)
            batch.end()
            fboG.end()
        }
        // FrameBuffer.end() автоматично відновлює GL viewport — не потрібно stage.viewport.apply()

        // ── Крок 3: застосовуємо ефект (blur тощо) ───────────────────────────
        // Підкласи типу ABlur реалізують свої FBO passes тут
        methods.applyEffect(batch, 1f)

        // ── Крок 4: фінальний рендер в fboResult ──────────────────────────────
        fboR.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.projectionMatrix = camera.combined
        batch.transformMatrix  = identityMatrix
        batch.begin()
        // Premultiplied alpha blend для запису в fboResult
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.color = Color.WHITE
        methods.renderFboResult(batch, 1f)
        // Скидаємо шейдер — підкласи встановлюють його в renderFboResult
        batch.shader = null
        batch.end()
        fboR.end()

        // ── Відновлюємо стан batch для caller ────────────────────────────────
        // viewport відновлено автоматично FrameBuffer.end()
        // тільки restore stage.viewport якщо він відрізняється від full screen
        stage?.viewport?.apply()
        batch.projectionMatrix = savedProjMatrix
        batch.transformMatrix  = identityMatrix
        batch.setBlendFunction(savedBlendSrc, savedBlendDst)
        batch.color = savedColor

        // Відновлюємо batch для наступного actor в AdvancedStage або
        // для батьківського preRenderChildren
        batch.begin()
    }

    // ── Static effect ──────────────────────────────────────────────────────────

    private fun shouldSkipRender(): Boolean {
        if (!isStaticEffect) return false
        staticCounter++
        return staticCounter > staticEffectFrames
    }

    // ── Frame Buffer ───────────────────────────────────────────────────────────

    protected open fun createFrameBuffer() {
        val w = width.toInt().coerceAtLeast(1)
        val h = height.toInt().coerceAtLeast(1)

        camera = OrthographicCamera(width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        // fboGroup — тільки якщо підклас його реально використовує
        if (useFboGroup) {
            fboGroup = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)
            textureGroup = TextureRegion(fboGroup!!.colorBufferTexture).apply { flip(false, true) }
        }

        fboResult = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)
        textureResult = TextureRegion(fboResult!!.colorBufferTexture).apply { flip(false, true) }
    }

    private fun recreateFrameBuffer() {
        disposeAll(fboGroup, fboResult)
        fboGroup  = null
        fboResult = null
        textureGroup  = null
        textureResult = null
        createFrameBuffer()
        // Дозволяємо перерендер після зміни розміру
        staticCounter = 0
    }

    // ── Helpers для підкласів (ABlur тощо) ────────────────────────────────────

    /**
     * Виконує блок з тимчасово заміненими матрицями batch.
     * Pre-allocated через локальні змінні — без GC.
     */
    protected inline fun Batch.withMatrix(newProj: Matrix4, newTrans: Matrix4, block: Block) {
        tmpProjMatrix.set(projectionMatrix)   // копіюємо значення
        tmpTransMatrix.set(transformMatrix)
        projectionMatrix = newProj
        transformMatrix  = newTrans
        block()
        projectionMatrix = tmpProjMatrix
        transformMatrix  = tmpTransMatrix
    }

    /**
     * Helper для підкласів — відкриває FBO, очищає, стартує batch.
     * Використовується в ABlur для blur passes.
     */
    protected fun FrameBuffer.beginFbo(batch: Batch) {
        if (batch.isDrawing) batch.end()
        begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.projectionMatrix = camera.combined
        batch.transformMatrix  = identityMatrix
        batch.begin()
    }

    /**
     * Helper для підкласів — закриває FBO, зупиняє batch.
     * НЕ відновлює viewport і матриці — це робить preRender() після всіх passes.
     */
    protected fun FrameBuffer.endFbo(batch: Batch) {
        if (batch.isDrawing) batch.end()
        end()
        // FrameBuffer.end() сам відновлює GL viewport до full screen
        // Фінальне відновлення stage viewport робить preRender() одноразово
    }
}