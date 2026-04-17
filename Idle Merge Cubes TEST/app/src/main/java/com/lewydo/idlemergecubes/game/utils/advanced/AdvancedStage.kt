package com.lewydo.idlemergecubes.game.utils.advanced

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup.renderPreRenderables

open class AdvancedStage(viewport: Viewport) : Stage(viewport) {

    fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        viewport.update(screenWidth, screenHeight, centerCamera)
        root.setSize(viewport.worldWidth, viewport.worldHeight)
    }

    fun render() {
        viewport.apply()
        act()

        // Фаза 1: preRender всіх PreRenderable в дереві
        // renderPreRenderables рекурсивно обходить дерево і знаходить
        // будь-які PreRenderable незалежно від глибини вкладення.
        batch.begin()
        actors.forEach { renderPreRenderables(it, batch, root.color.a) } // 🧠 Попередній рендеринг FBO-груп
        batch.end()

        // Фаза 2: стандартний LibGDX draw
        draw()
    }

    override fun dispose() {
        actors.onEach { actor -> if (actor is Disposable) actor.dispose() }
        super.dispose()
    }

}