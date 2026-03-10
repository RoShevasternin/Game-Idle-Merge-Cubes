package com.lewydo.idlemergecubes.game.actors.progress

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.createShader
import com.lewydo.idlemergecubes.game.utils.gdxGame
import kotlin.math.abs

class ACircleProgress(
    override val screen: AdvancedScreen,
    progressPercent: Float = 100f, // -100..100

    var startAngle  : Float = 0f, // 0..360
    var emptyPercent: Float = 0f, // 0..100
) : PreRenderableGroup() {

    companion object {
        private val shaderProgram: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/circleProgress/circleProgressFS.glsl"
            )
        }
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------

    var progressPercent: Float = progressPercent
        private set

    private var targetProgress = progressPercent

    // швидкість наближення (чим більше — тим швидше)
    private var smoothSpeed = 6f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun act(delta: Float) {
        super.act(delta)
        updateProgress(delta)
    }

    // ------------------------------------------------------------------------
    // Shader render
    // ------------------------------------------------------------------------

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            batch.shader = shaderProgram

            shaderProgram.setUniformf("u_progress", progressPercent)
            shaderProgram.setUniformf("u_innerEmpty", emptyPercent)

            shaderProgram.setUniformf("u_startAngle", startAngle)
            shaderProgram.setUniformf("u_roundness", 1.0f)

            shaderProgram.setUniformf("u_colorStart", GameColor.progressStart)
            shaderProgram.setUniformf("u_colorEnd", GameColor.progressEnd)

            batch.draw(textureGroup, 0f, 0f, width, height)

        }
    }

    // ------------------------------------------------------------------------
    // Progress logic
    // ------------------------------------------------------------------------

    private fun updateProgress(delta: Float) {

        if (progressPercent == targetProgress) return

        progressPercent = MathUtils.lerp(
            progressPercent,
            targetProgress,
            smoothSpeed * delta
        )

        if (abs(progressPercent - targetProgress) < 0.01f) {
            progressPercent = targetProgress
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setProgress(percent: Float) {
        targetProgress = percent.coerceIn(-100f, 100f)
    }

    fun setProgressInstant(percent: Float) {
        val value = percent.coerceIn(-100f, 100f)
        targetProgress = value
        progressPercent = value
    }

}