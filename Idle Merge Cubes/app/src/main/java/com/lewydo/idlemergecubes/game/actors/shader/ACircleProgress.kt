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

class ACircleProgress(override val screen: AdvancedScreen) : PreRenderableGroup() {

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

    var progressPercent = -75f

    // Використовуй Float для кутів у LibGDX
    var startAngle   = 0f
    var emptyPercent = 85f

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {

            batch.shader = shaderProgram

            // ВАРІАНТ А: Передаємо 0..100 (як чекає твій шейдер)
            shaderProgram.setUniformf("u_progress", progressPercent)

            // ВАРІАНТ Б: Якщо хочеш передавати 0..1, змініть шейдер (прибрати /100.0)
            // shaderProgram.setUniformf("u_progress", progressPercent / 100f)

            shaderProgram.setUniformf("u_startAngle", startAngle)

            // Використовуємо значення зі змінної, а не хардкод 0.75
            shaderProgram.setUniformf("u_innerEmpty", emptyPercent / 100f)

            shaderProgram.setUniformf("u_colorStart", GameColor.progressStart)
            shaderProgram.setUniformf("u_colorEnd", GameColor.progressEnd)

            shaderProgram.setUniformf("u_roundness", 1.0f)

            batch.draw(textureGroup, 0f, 0f, width, height)
        }
    }

}