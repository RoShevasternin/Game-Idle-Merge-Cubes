package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.lewydo.idlemergecubes.game.actors.button.ABuyButton
import com.lewydo.idlemergecubes.game.actors.panel.APanelTop
import com.lewydo.idlemergecubes.game.actors.panelGrid.APanelGrid
import com.lewydo.idlemergecubes.game.actors.panelIdle.APanelIdle
import com.lewydo.idlemergecubes.game.actors.progress.AProgressDefault
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.HAlign
import com.lewydo.idlemergecubes.game.utils.actor.VAlign
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameScreen: AdvancedScreen() {
    private val aProgressTest = AProgressDefault(this)

    private val aPanelTop  = APanelTop(this)
    private val aPanelGame = APanelGrid(this)
    private val aPanelIdle = APanelIdle(this)

    private val aBuyBtn = ABuyButton(this)

    override fun show() {
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()
    }

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addPanelGame()
        addPanelIdle()
        addBuyBtn()

        //addProgressTest()

        animShowScreen()
    }

    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // Actors ------------------------------------------------------------------------

    private fun Group.addPanelTop() {
        aPanelTop.setSize(2160f, 467f)
        addActorAligned(aPanelTop, HAlign.CENTER, VAlign.TOP)

        var isVisible = true
        aPanelTop.setOnClickListener {
            if (isVisible) {
                animHideScreen()
            } else {
                animShowScreen()
            }
            isVisible = !isVisible
        }
    }

    private fun Group.addPanelGame() {
        aPanelGame.setSize(1905f, 1905f)
        addActorWithConstraints(aPanelGame) {
            startToStartOf   = this@addPanelGame
            endToEndOf       = this@addPanelGame
            topToBottomOf    = aPanelTop

            marginTop = 238f
        }
    }

    private fun Group.addPanelIdle() {
        aPanelIdle.setSize(1905f, 428f)
        addActorWithConstraints(aPanelIdle) {
            startToStartOf   = this@addPanelIdle
            endToEndOf       = this@addPanelIdle
            topToBottomOf    = aPanelGame

            marginTop = 151f
        }
    }

    private fun Group.addBuyBtn() {
        aBuyBtn.setSize(1107f, 345f)
        addActorWithConstraints(aBuyBtn) {
            startToStartOf   = this@addBuyBtn
            endToEndOf       = this@addBuyBtn
            bottomToBottomOf = this@addBuyBtn

            marginBottom = 206f
        }


        aBuyBtn.setOnClickListener {
            gdxGame.modelGrid.addCube(1)
        }
    }

    private fun Group.addProgressTest() {
        aProgressTest.setSize(1500f, 580f)
        addActorAligned(aProgressTest, HAlign.CENTER, VAlign.CENTER)


        val a = listOf(1, 100, 1000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000_000, 10_000_000_000_000)
        coroutine?.launch {
            aProgressTest.progressPercentFlow.collect { percent ->
                runGDX {
                    //gdxGame.modelPlayer.addXp(percent.toLong())
                }

                val index = (percent / 10).toInt().coerceIn(0, a.size - 1)
                testFlow.value = a[index].toString()
            }
        }
    }

}

val testFlow = MutableStateFlow("1")