package com.lewydo.rozval.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group

interface PreRenderable {
    fun preRender(batch: Batch, parentAlpha: Float)
}

fun renderPreRenderables(actor: Actor, batch: Batch, parentAlpha: Float) {
    if (actor.isVisible.not()) return

    val currentAlpha = actor.color.a * parentAlpha

    when (actor) {
        is PreRenderable -> {
            actor.preRender(batch, currentAlpha)
        }

        is Group -> {
            actor.children.forEach { child -> renderPreRenderables(child, batch, currentAlpha) }
        }
    }
}