package com.lewydo.idlemergecubes.game.actors.panelIdle

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.utils.ShaderClock
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.lewydo.idlemergecubes.game.utils.createShader
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ALavaProgress(
    override val screen: AdvancedScreen
) : PreRenderableGroup() {

    companion object {
        private val shaderProgram: ShaderProgram by lazy {
            createShader(
                "shader/defaultVS.glsl",
                "shader/lavaProgress/lavaProgressFS.glsl"
            )
        }
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------

    private var edgeDeform = 1f
    private var finishFlash = 0f

    private var finishing = false
    private var finishTimer = 0f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        addAndFillActor(Image(gdxGame.assetsAll.idle_progress))
    }

    // ------------------------------------------------------------------------
    // Update
    // ------------------------------------------------------------------------

    override fun act(delta: Float) {
        super.act(delta)

        if (!finishing) return

        finishTimer += delta

        val progress = (finishTimer / 0.4f).coerceAtMost(1f)

        // jelly calm down
        edgeDeform = 1f - progress

        // flash animation
        finishFlash = if (finishTimer < 0.12f) {
            finishTimer / 0.12f
        } else {
            1f - ((finishTimer - 0.12f) / 0.35f).coerceAtMost(1f)
        }

        if (progress >= 1f) finishing = false
    }

    // ------------------------------------------------------------------------
    // Shader render
    // ------------------------------------------------------------------------

    override fun getPreRenderMethods() = object : PreRenderMethods {

        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            drawChildrenWithoutTransform(batch, parentAlpha)
        }

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {

            batch.shader = shaderProgram

            shaderProgram.setUniformf("u_time", ShaderClock.time)
            shaderProgram.setUniformf("u_edgeDeform", edgeDeform)
            shaderProgram.setUniformf("u_finishFlash", finishFlash)

            batch.draw(textureGroup, 0f, 0f, width, height)
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun reset() {
        finishing = false
        finishTimer = 0f

        edgeDeform = 1f
        finishFlash = 0f
    }

    fun finish() {
        finishing = true
        finishTimer = 0f
    }
}