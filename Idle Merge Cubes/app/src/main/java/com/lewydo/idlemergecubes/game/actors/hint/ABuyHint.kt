package com.lewydo.idlemergecubes.game.actors.hint

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.progress.AProgressBuyHint
import com.lewydo.idlemergecubes.game.model.BuyLevelModel
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ABuyHint(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoMedium, 45f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    // Ліва частина — іконка замка + текст
    private val aLockImg  = Image(gdxGame.assetsAll.lock)
    private val aTextLbl  = AMsdfLabel("Reach cube 0 -> BUY lv.0 ", styleDef)

    // Права частина — progress bar + лічильник
    private val aProgress = AProgressBuyHint(screen)
    private val aCountLbl = AMsdfLabel("0/2", styleDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addLockImg()
        addTextLbl()
        addProgress()
        addCountLbl()

        collectBuyLevel()
        collectUpgradeEvent()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLockImg() {
        addActor(aLockImg)
        aLockImg.setBounds(0f, 16f, 45f, 45f)
    }

    private fun addTextLbl() {
        addActor(aTextLbl)
        aTextLbl.setBounds(61f, 0f, 541f, 61f)
    }

    private fun addProgress() {
        addActor(aProgress)
        aProgress.setBounds(746f, 20f, 1020f, 20f)
    }

    private fun addCountLbl() {
        addActor(aCountLbl)
        aCountLbl.setBounds(1796f, 0f, 68f, 61f)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectBuyLevel() {
        coroutine?.launch {
            combine(
                gdxGame.modelBuyLevel.buyLevelFlow,
                gdxGame.modelBuyLevel.nextTargetFlow,
                gdxGame.modelBuyLevel.progressFlow,
                gdxGame.modelGrid.gridFlow,
            ) { level, target, progress, grid ->
                val maxCube = grid.filter { it > 0 }.maxOrNull() ?: 0
                val (current, total) = BuyLevelModel.tierCubeProgress(maxCube, level)
                Data(level, target, progress, current, total)
            }.collect { data ->
                runGDX { updateUI(data) }
            }
        }
    }

    private fun collectUpgradeEvent() {
        coroutine?.launch {
            GlobalEvents.events
                .filter { it == GlobalEvents.EventType.BUY_LEVEL_UPGRADED }
                .collect { runGDX { animUpgrade() } }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private var isFirstUpdate = true

    private fun updateUI(data: Data) {
        aTextLbl.setText("Reach cube ${data.nextTarget} -> BUY lv.${data.buyLevel + 1}")
        aCountLbl.setText("${data.current}/${data.total}")

        val percent = data.progress * 100f
        aProgress.updateProgress(percent, instant = isFirstUpdate)
        isFirstUpdate = false
    }

    // Маленька анімація при апгрейді — хінт оновлюється
    private fun animUpgrade() {
        clearActions()
        addAction(Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, 0.15f, Interpolation.sineOut),
            Actions.scaleTo(1.0f,  1.0f,  0.20f, Interpolation.sineOut),
        ))
    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    private data class Data(
        val buyLevel  : Int,
        val nextTarget: Int,
        val progress  : Float,
        val current   : Int,
        val total     : Int,
    )
}