package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.runGDX

class ARadialGradient(
    override val screen: AdvancedScreen,
    val startColor: Color,
    val endColor  : Color,
    val centerXPercent: Float = 0.5f,
    val centerYPercent: Float = 0.5f,
    val radiusXPercent : Float = 0.5f,
    val radiusYPercent : Float = 0.5f,
    val angle    : Float = 0f,
    val timeAnim : Float = 0f,
    val direction: Static.Direction = Static.Direction.START_END
): AdvancedGroup() {

    companion object {
        private var vertexShader   = Gdx.files.internal("shader/gradient/defaultVS.glsl").readString()
        private var fragmentShader = Gdx.files.internal("shader/gradient/radialFS.glsl").readString()
    }

    private var shaderProgram: ShaderProgram? = null
    private var time = 0f

    override fun addActorsOnGroup() {
        runGDX {
            createShader()
            addAndFillActor(Image(screen.drawerUtil.getTexture(Color.WHITE)))
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null || shaderProgram == null) return

        batch.shader = shaderProgram

        timeCounter()

        shaderProgram!!.setUniformf("u_startColor", startColor.r, startColor.g, startColor.b, startColor.a)
        shaderProgram!!.setUniformf("u_endColor", endColor.r, endColor.g, endColor.b, endColor.a)
        shaderProgram!!.setUniformf("u_time", time)
        shaderProgram!!.setUniformf("u_cycleTime", timeAnim)
        shaderProgram!!.setUniformf("u_resolution", width, height)
        shaderProgram!!.setUniformf("u_center", width * centerXPercent, height * centerYPercent)
        shaderProgram!!.setUniformf("u_radius", width * radiusXPercent, width * radiusYPercent)
        shaderProgram!!.setUniformf("u_angle", angle)
        shaderProgram!!.setUniformi("u_animate", if (timeAnim > 0f) 1 else 0)

        super.draw(batch, parentAlpha)
        batch.shader = null
    }

    override fun dispose() {
        shaderProgram?.dispose()
        super.dispose()
    }

    // Logic ------------------------------------------------------------------------

    private fun createShader() {
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        if (!shaderProgram!!.isCompiled) {
            throw IllegalStateException("Shader compilation failed:\n" + shaderProgram!!.log)
        }
    }

    private fun timeCounter() {
        if (timeAnim > 0f) {
            when(direction) {
                Static.Direction.START_END -> {
                    time -= Gdx.graphics.deltaTime
                    if (time < -timeAnim) time += timeAnim
                }
                Static.Direction.END_START -> {
                    time += Gdx.graphics.deltaTime
                    if (time > timeAnim) time -= timeAnim
                }
            }
        }
    }

    object Static {
        enum class Direction {
            START_END, END_START
        }
    }

}