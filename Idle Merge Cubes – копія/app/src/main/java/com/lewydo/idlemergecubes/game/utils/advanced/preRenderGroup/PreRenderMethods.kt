package com.lewydo.idlemergecubes.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.g2d.Batch

/** -------------------------------------------------------------------------
// Визначає три фази FBO-рендеру для [PreRenderableGroup].
// Порядок викликів в [PreRenderableGroup.preRender]:
//   1. [renderFboGroup]  — малює вміст групи в fboGroup (позиція (0,0), camera-простір)
//   2. [applyEffect]     — застосовує ефект (blur, і т.д.) між fboGroup → проміжні FBO
//   3. [renderFboResult] — малює фінальний результат в fboResult
// Важливо: всі три методи рендерять в FBO-просторі (alpha = 1f).
// Реальна [parentAlpha] застосовується пізніше в [PreRenderableGroup.draw].
// ------------------------------------------------------------------------- */

interface PreRenderMethods {

    fun renderFboGroup (batch: Batch, parentAlpha: Float) {}
    fun applyEffect    (batch: Batch, parentAlpha: Float) {}
    fun renderFboResult(batch: Batch, parentAlpha: Float)

}