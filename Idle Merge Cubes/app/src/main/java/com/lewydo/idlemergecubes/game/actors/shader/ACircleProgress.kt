package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.disposeAll
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACircleProgress(
    override val screen: AdvancedScreen,
    var progressPercent : Float = 100f, // -100..100
    var startAngle      : Float = 0f, // 0..360
    var emptyPercent    : Float = 0f, // 0..100
) : PreRenderableGroup() {

    companion object {
        // ShaderProgram.pedantic = false краще винести в блок ініціалізації
        private val shaderProgram: ShaderProgram by lazy {
            val vertex = Gdx.files.internal("shader/defaultVS.glsl").readString()
            val fragment = Gdx.files.internal("shader/circleProgress/circleProgressFS.glsl").readString()

            ShaderProgram(vertex, fragment).apply {
                if (!isCompiled) throw IllegalStateException("Shader failed: $log")
                gdxGame.disposableSet.add(this)
            }
        }
    }

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {

            batch.shader = shaderProgram

            // Тепер скрізь просто відсотки 0..100
            shaderProgram.setUniformf("u_progress", progressPercent)
            shaderProgram.setUniformf("u_innerEmpty", emptyPercent)

            shaderProgram.setUniformf("u_startAngle", startAngle)
            shaderProgram.setUniformf("u_roundness", 1.0f) // 1.0 - ідеальне коло на кінцях

            shaderProgram.setUniformf("u_colorStart", GameColor.progressStart)
            shaderProgram.setUniformf("u_colorEnd", GameColor.progressEnd)

            batch.draw(textureGroup, 0f, 0f, width, height)
        }
    }

}