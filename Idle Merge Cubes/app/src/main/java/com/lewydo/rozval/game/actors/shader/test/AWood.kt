package com.lewydo.rozval.game.actors.shader.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.rozval.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.rozval.game.utils.disposeAll
import com.lewydo.rozval.game.utils.font.FontParameter
import com.lewydo.rozval.game.utils.gdxGame

open class AWood(override val screen: AdvancedScreen): PreRenderableGroup() {

    companion object {
        // Завантажуємо один раз для всіх
        private val shaderProgram by lazy {
            val vs = Gdx.files.internal("shader/defaultVS.glsl").readString()
            val fs = Gdx.files.internal("shader/blockFS.glsl").readString()

            ShaderProgram.pedantic = true
            ShaderProgram(vs, fs).apply { if (!isCompiled) throw IllegalStateException("Shader failed: $log") }
        }
    }

    var damage: Float = 0f // 0..100

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addAndFillActor(Image(gdxGame.assetsAll.WOOD))
    }

    override fun dispose() {
        super.dispose()
        disposeAll(
            shaderProgram,
        )
    }

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            drawChildrenWithoutTransform(batch, parentAlpha)
        }

        override fun applyEffect(batch: Batch, parentAlpha: Float) {}

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            batch.shader = shaderProgram
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
            textureGroup!!.texture.bind(0)
            shaderProgram.setUniformf("u_damage", damage / 100f)

            //batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            batch.draw(textureGroup, 0f, 0f, width, height)
        }
    }

}