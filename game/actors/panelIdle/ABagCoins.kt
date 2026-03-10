package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.setOrigin
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.createShader
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ABagCoins(override val screen: AdvancedScreen) : PreRenderableGroup() {

    companion object {
        private val shaderProgram: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/bagCoins/bagCoinsFS.glsl"
            )
        }
    }

    private var fillPercent = 0f

    private var fillDuration = 10f
    private var elapsed = 0f

    private var isFilling = false

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addAndFillActor(Image(gdxGame.assetsAll.bag_coins))
    }

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            drawChildrenWithoutTransform(batch, parentAlpha)
        }

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {

            batch.shader = shaderProgram
            shaderProgram.setUniformf("u_fillPercent", fillPercent)
            batch.draw(textureGroup, 0f, 0f, width, height)

        }
    }

    override fun act(delta: Float) {
        super.act(delta)

        if (!isFilling) return

        elapsed += delta

        fillPercent = (elapsed / fillDuration) * 100f

        if (fillPercent >= 100f) {
            fillPercent = 100f
            isFilling = false
        }
    }

    fun startFill(seconds: Float) {
        fillDuration = seconds
        elapsed = 0f
        fillPercent = 0f
        isFilling = true
    }

    fun reset() {
        elapsed = 0f
        fillPercent = 0f
        isFilling = false
    }

}