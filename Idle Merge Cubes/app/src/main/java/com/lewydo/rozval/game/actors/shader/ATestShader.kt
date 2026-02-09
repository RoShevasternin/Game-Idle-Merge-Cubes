package com.lewydo.rozval.game.actors.shader

import com.badlogic.gdx.graphics.g2d.Batch
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.advanced.preRenderGroup.PreRenderMethods
import com.lewydo.rozval.game.utils.advanced.preRenderGroup.PreRenderableGroup

class ATestShader(override val screen: AdvancedScreen): PreRenderableGroup() {

    override fun getPreRenderMethods() = object : PreRenderMethods {
        override fun renderFboGroup(batch: Batch, parentAlpha: Float) {
            drawChildrenWithoutTransform(batch, parentAlpha)
        }

        override fun renderFboResult(batch: Batch, parentAlpha: Float) {
            batch.draw(textureGroup, 0f, 0f)
        }

    }

}