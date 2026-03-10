package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.captureScreenShot
import com.lewydo.idlemergecubes.util.currentClassName

class ABlurBack(
    override val screen: AdvancedScreen,
    maskTexture: Texture? = null
): AdvancedGroup() {

    private lateinit var regionScreenShot: TextureRegion

    private val boundsScreenShot = Rectangle()

    private val aMask = AMask(screen, maskTexture)
    private val aBlur = ABlur(screen)

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
        updateBoundsScreenShot()
        regionScreenShot = TextureRegion(Texture(boundsScreenShot.width.toInt(), boundsScreenShot.height.toInt(), Pixmap.Format.RGB888)).apply { flip(false, true) }

        addAndFillActor(aMask)
        aMask.addAndFillActor(aBlur)
        aBlur.addAndFillActor(Image(regionScreenShot))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (aBlur.isBlurEnabled.not()) return
        if (batch == null) throw Exception("Error draw: ${this::class.simpleName}")

        updateBoundsScreenShot()
        captureScreenShot(
            regionScreenShot,
            boundsScreenShot.x.toInt(),
            boundsScreenShot.y.toInt(),
            boundsScreenShot.width.toInt(),
            boundsScreenShot.height.toInt()
        )

        super.draw(batch, parentAlpha)
    }

    private fun updateBoundsScreenShot() {
        vecGroupPosition.set(localToStageCoordinates(vecTmp.set(0f, 0f)))

        // Отримуємо екранні координати (перетворюємо їх у пікселі)
        val bottomLeft = vecBottomLeft.set(vecGroupPosition.x, vecGroupPosition.y)
        val topRight   = vecTopRight.set(vecGroupPosition.x + width, vecGroupPosition.y + height)

        // Конвертуємо координати з FitViewport у ScreenViewport
        screen.viewportUI.project(bottomLeft)
        screen.viewportUI.project(topRight)

        // Отримуємо екранні значення
        val screenX = bottomLeft.x
        val screenY = bottomLeft.y
        val screenW = topRight.x - bottomLeft.x
        val screenH = topRight.y - bottomLeft.y

        //log("screenX = $screenX, screenY = $screenY, screenW = $screenW, screenH = $screenH")

        boundsScreenShot.set(screenX, screenY, screenW, screenH)
        //return Rectangle(0f, 0f, screenW, screenH)
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