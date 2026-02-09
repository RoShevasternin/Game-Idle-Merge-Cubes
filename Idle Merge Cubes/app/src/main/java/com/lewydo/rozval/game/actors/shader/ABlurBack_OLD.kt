package com.lewydo.rozval.game.actors.shader

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.ScreenUtils
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.getTopParent
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.util.currentClassName

class ABlurBack_OLD(
    override val screen: AdvancedScreen,
    private val maskTexture: Texture? = null
): AdvancedGroup() {

    private val identityMatrix: Matrix4 = Matrix4().idt()
    private var camera = OrthographicCamera()

    private var fboSceneBack: FrameBuffer?   = null
    private var fboSceneUI  : FrameBuffer?   = null
    private var fboScene    : FrameBuffer?   = null

    private var textureSceneBack: TextureRegion? = null
    private var textureSceneUI  : TextureRegion? = null
    private var textureScene    : TextureRegion? = null

    private val aMask = AMask(screen, maskTexture)
     val aBlur = ABlur(screen)

    var radiusBlur = 0f
        set(value) {
            aBlur.radiusBlur = value
            field = value
        }

    var isStaticEffect = false
        set(value) {
            aBlur.isStaticEffect = value
            aMask.isStaticEffect = value
            field = value
        }

    private val vecTmp           = Vector2(0f, 0f)
    private val vecGroupPosition = Vector2()
    private val vecBottomLeft    = Vector2()
    private val vecTopRight      = Vector2()

    override fun addActorsOnGroup() {
        createFrameBuffer()

        addAndFillActor(aMask)
        aMask.addAndFillActor(aBlur)
        aBlur.addAndFillActor(Image(textureScene))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (aBlur.isBlurEnabled.not()) return

        if (batch == null) throw Exception("Error draw: ${this::class.simpleName}")

        batch.end()
        captureScreenBack(batch)
        captureScreenUI(batch)
        captureScreenAll(batch)

/** ОПТИМІЗАЦІЯ - НЕ РЕНДЕР РІЗНІ СЛОЇ (BACK | UI) А РОБИМ ScreenShot!!! */
//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//
//        fun captureFromScreenRegion(x: Int, y: Int, w: Int, h: Int) {
//            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, textureScene!!.texture.textureObjectHandle)
//            Gdx.gl20.glCopyTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, x, y, w, h)
//        }
//
//        captureFromScreenRegion(x.toInt(),y.toInt(),1200,550)

        batch.begin()

        super.draw(batch, parentAlpha)
    }

    private fun createFrameBuffer() {
        camera = OrthographicCamera(width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        fboSceneBack = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
        fboSceneUI   = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
        fboScene     = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)

        textureSceneBack = TextureRegion(fboSceneBack!!.colorBufferTexture).apply { flip(false, true) }
        textureSceneUI   = TextureRegion(fboSceneUI!!.colorBufferTexture).apply { flip(false, true) }
        textureScene     = TextureRegion(fboScene!!.colorBufferTexture).apply { flip(false, true) }
    }

    private fun captureScreenBack(batch: Batch) {
        screen.viewportBack.apply()
        vecGroupPosition.set(localToStageCoordinates(vecTmp.set(0f, 0f)))

        // Отримуємо екранні координати (перетворюємо їх у пікселі)
        val bottomLeft = vecBottomLeft.set(vecGroupPosition.x, vecGroupPosition.y)
        val topRight   = vecTopRight.set(vecGroupPosition.x + width, vecGroupPosition.y + height)

        // Конвертуємо координати з FitViewport у ScreenViewport
        screen.viewportUI.project(bottomLeft)
        screen.viewportUI.project(topRight)

        // Отримуємо екранні значення
        val screenX      = bottomLeft.x
        val screenY      = bottomLeft.y
        val screenWidth  = topRight.x - bottomLeft.x
        val screenHeight = topRight.y - bottomLeft.y

        camera.setToOrtho(false, screenWidth, screenHeight)
        camera.position.set(screenX + screenWidth / 2f, screenY + screenHeight / 2f, 0f)
        camera.update()

        // 1. Захоплюємо всю сцену до рендеру групи
        fboSceneBack!!.beginAdvanced(batch)
        //batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            isVisible = false
            screen.stageBack.root.draw(batch, 1f)
            isVisible = true
        }

        fboSceneBack!!.endAdvanced(batch)
    }

    private fun captureScreenUI(batch: Batch) {
        screen.stageUI.viewport.apply()
        vecGroupPosition.set(localToStageCoordinates(vecTmp.set(0f, 0f)))

        camera.setToOrtho(false, width, height)
        camera.position.set(vecGroupPosition.x + (width / 2f), vecGroupPosition.y + (height / 2f), 0f)
        camera.update()

        // 1. Захоплюємо всю сцену до рендеру групи
        fboSceneUI!!.beginAdvanced(batch)
        //batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            val currentTopParent = getTopParent(screen.stageUI.root)

            if (currentTopParent == screen.stageUI.root) {
                isVisible = false
                drawChildrenUpTo(batch, 1f, screen.stageUI.root, this)
                isVisible = true
            } else {
                currentTopParent.isVisible = false
                drawChildrenUpTo(batch, 1f, screen.stageUI.root, currentTopParent)
                currentTopParent.isVisible = true
            }
        }

        fboSceneUI!!.endAdvanced(batch)
    }

    private fun captureScreenAll(batch: Batch) {
        camera.setToOrtho(false, width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        fboScene!!.beginAdvanced(batch)
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            batch.draw(textureSceneBack, 0f, 0f, width, height)
            batch.draw(textureSceneUI, 0f, 0f, width, height)
        }

        fboScene!!.endAdvanced(batch)
    }

    private inline fun Batch.withMatrix(newProjectionMatrix: Matrix4, newTransformMatrix: Matrix4, block: () -> Unit) {
        val oldProj  = projectionMatrix
        val oldTrans = transformMatrix
        projectionMatrix = newProjectionMatrix
        transformMatrix  = newTransformMatrix
        block()
        projectionMatrix = oldProj
        transformMatrix  = oldTrans
    }

    private fun FrameBuffer.beginAdvanced(batch: Batch) {
        begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.begin()
    }

    private fun FrameBuffer.endAdvanced(batch: Batch) {
        batch.end()
        end()

        stage.viewport.apply()

        batch.projectionMatrix = stage.camera.combined
        batch.transformMatrix  = identityMatrix

        batch.color = Color.WHITE
        batch.shader = null
    }

    private fun drawChildrenUpTo(batch: Batch, parentAlpha: Float, root: Group, stopAt: Actor) {
        root.children.begin()
        for (i in 0 until root.children.size) {
            val child = root.children[i]

            if (child === stopAt) break

            batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
            child.draw(batch, parentAlpha)
        }
        root.children.end()
    }

    // rotate | scale - Only mask ------------------------------------------------------------

    override fun getRotation() = 0f
    override fun setRotation(degrees: Float) {
        throw Exception("$currentClassName - не обертається | NOT - setRotation")
    }
    override fun rotateBy(amountInDegrees: Float) {
        throw Exception("$currentClassName - не обертається | NOT - rotateBy")
    }
    override fun getScaleX(): Float = 1f
    override fun getScaleY(): Float = 1f

}