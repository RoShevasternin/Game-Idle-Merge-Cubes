package com.lewydo.idlemergecubes.game.actors.progress

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.vfx.AMask
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AProgressBuyHint(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val LENGTH      = 1016f
    private val onePercentX = LENGTH / 100f

    private val backgroundImage = Image(gdxGame.assetsAll.progress_frame_buy_hint)
    private val progressImage   = Image(gdxGame.assetsAll.progress_buy_hint)
    private val mask            = AMask(screen, gdxGame.assetsAll.MASK_PROGRESS_BUY_HINT)

    override fun addActorsOnGroup() {
        addAndFillActor(backgroundImage)
        addMask()
    }

    private fun AdvancedGroup.addMask() {
        addActor(mask)
        mask.setBounds(2f, 2f, LENGTH, 16f)
        mask.addAndFillActor(progressImage)
        progressImage.x = -LENGTH
    }

    // ------------------------------------------------------------------------
    // Public API — єдиний метод
    // ------------------------------------------------------------------------

    // Викликати при кожному оновленні прогресу
    // Якщо newPercent < поточний — значить тир пройдений:
    //   1. Доповнити до 100%
    //   2. Скинути в 0
    //   3. Заповнити до нового значення
    fun updateProgress(newPercent: Float, instant: Boolean = false) {
        val currentPercent = ((progressImage.x + LENGTH) / onePercentX)

        if (instant) {
            progressImage.clearActions()
            progressImage.x = targetX(newPercent)
            return
        }

        if (newPercent < currentPercent - 1f) {
            // Тир пройдений — анімація завершення + скид + новий тир
            progressImage.clearActions()
            progressImage.addAction(Actions.sequence(
                Actions.moveTo(targetX(100f), progressImage.y, 0.2f, Interpolation.sineOut),
                Actions.moveTo(targetX(0f),   progressImage.y, 0f),
                Actions.moveTo(targetX(newPercent), progressImage.y, 0.4f, Interpolation.sineOut),
            ))
        } else {
            // Звичайне заповнення
            progressImage.clearActions()
            progressImage.addAction(
                Actions.moveTo(targetX(newPercent), progressImage.y, 0.5f, Interpolation.sineOut)
            )
        }
    }

    private fun targetX(percent: Float) = (percent * onePercentX) - LENGTH
}