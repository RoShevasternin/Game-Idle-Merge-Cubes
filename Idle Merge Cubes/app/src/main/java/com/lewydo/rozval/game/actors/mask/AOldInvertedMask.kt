package com.lewydo.rozval.game.actors.mask

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.zeroScreenVector

class AOldInvertedMask(
    override val screen: AdvancedScreen,
    private val region: TextureRegion,
) : AdvancedGroup() {

    private lateinit var screenCoordinates: Vector2
    private lateinit var screenSize       : Vector2

    private var mX: Float = 0f
    private var mY: Float = 0f
    private var mW: Float = 0f
    private var mH: Float = 0f

    override fun addActorsOnGroup() {}

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (stage != null) {
            batch?.flush()

            updateCoordinatesAndSize()
            drawScissor(batch, parentAlpha)
        }
    }


    private fun drawSuper(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }

    private fun updateCoordinatesAndSize() {
        screenCoordinates = localToScreenCoordinates(Vector2())
        screenSize        = stage.viewport.project(Vector2(width, height))

        mX = screenCoordinates.x
        mY = Gdx.graphics.height - screenCoordinates.y
        mW = screenSize.x - stage.viewport.zeroScreenVector.x
        mH = screenSize.y - stage.viewport.zeroScreenVector.y
    }

    private fun drawScissor(batch: Batch?, parentAlpha: Float) {
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST)
        Gdx.gl.glScissor(mX.toInt(), mY.toInt(), mW.toInt(), mH.toInt())

        batch?.drawMask(parentAlpha)
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST)
    }

    private fun Batch.drawMask(parentAlpha: Float) {
        Gdx.gl.glColorMask(false, false, false, true)
        setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA)

        drawSuper(this, parentAlpha)
        drawMasked()
    }

    private fun Batch.drawMasked() {
        setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_ALPHA)
        draw(region, x, y, width, height)
        flush()

        Gdx.gl.glColorMask(true, true, true, true)
        setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA)
        draw(region, x, y, width, height)
        flush()

        setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

}