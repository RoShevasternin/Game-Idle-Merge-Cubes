package com.lewydo.rozval.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.g2d.Batch

interface PreRenderMethods {

    fun renderFboGroup(batch: Batch, parentAlpha: Float) {}
    fun applyEffect(batch: Batch, parentAlpha: Float) {}
    fun renderFboResult(batch: Batch, parentAlpha: Float)

}