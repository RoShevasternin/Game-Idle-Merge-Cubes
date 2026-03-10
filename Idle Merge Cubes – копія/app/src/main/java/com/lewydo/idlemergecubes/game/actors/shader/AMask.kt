package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.createShader
import com.lewydo.idlemergecubes.game.utils.disposeAll
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AMask(
    override val screen: AdvancedScreen,
    private val maskTexture: Texture? = null
) : PreRenderableGroup() {

    companion object {
        private val shaderProgram: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/mask/maskFS.glsl"
            )
        }
    }

    override fun getPreRenderMethods() = object : PreRenderMethods {
        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            drawChildrenWithoutTransform(batch, parentAlpha)
        }

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            //batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            if (maskTexture != null) {
                batch.shader = shaderProgram

                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
                maskTexture.bind(1)
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
                textureGroup!!.texture.bind(0)

                shaderProgram.setUniformi("u_mask", 1)
                shaderProgram.setUniformi("u_texture", 0)
            }

            batch.draw(textureGroup, 0f, 0f, width, height)
        }
    }

    //globalPosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))
    //camera.position.set(globalPosition.x + width / 2f, globalPosition.y + height / 2f, 0f)
    //camera.update()

    //SpriteBatch().setBlendFunction()
    //batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
    //batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    //batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE)

}