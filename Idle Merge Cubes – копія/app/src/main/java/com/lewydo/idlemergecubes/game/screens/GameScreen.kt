package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.ABuyButton
import com.lewydo.idlemergecubes.game.actors.panel.APanelTop
import com.lewydo.idlemergecubes.game.actors.panelGrid.APanelGrid
import com.lewydo.idlemergecubes.game.actors.panelIdle.APanelIdle
import com.lewydo.idlemergecubes.game.actors.panelMenu.APanelMenu
import com.lewydo.idlemergecubes.game.actors.progress.AProgressDefault
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.WIDTH_UI
import com.lewydo.idlemergecubes.game.utils.actor.HAlign
import com.lewydo.idlemergecubes.game.utils.actor.VAlign
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.enable
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aProgressTest = AProgressDefault(this)

    private val aPanelTop  = APanelTop(this)
    private val aPanelGrid = APanelGrid(this)
    private val aPanelIdle = APanelIdle(this)

    private val aBuyBtn = ABuyButton(this)

    private val aDimImg    = Image(drawerUtil.getTexture(GameColor.black_55))
    private val aPanelMenu = APanelMenu(this)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
//    private var isOpenMenu = false

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
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

        addDimImg()
        addPanelMenu()

        //addProgressTest()

        animShowScreen()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun Group.addPanelTop() {
        aPanelTop.setSize(2160f, 467f)
        addActorAligned(aPanelTop, HAlign.CENTER, VAlign.TOP)

        aPanelTop.onClickSettingsBtn = { animShowMenu() }



        var isTestVisible = true
        aPanelTop.setOnClickListener {
            if (isTestVisible) {
                animHideScreen()
            } else {
                animShowScreen()
            }
            isTestVisible = !isTestVisible
        }
    }

    private fun Group.addPanelGame() {
        aPanelGrid.setSize(1905f, 1905f)
        addActorWithConstraints(aPanelGrid) {
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
            topToBottomOf    = aPanelGrid

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
            aPanelGrid.buyCube()
        }
    }

    private fun Group.addPanelMenu() {
        aPanelMenu.disable()
        aPanelMenu.setSize(WIDTH_UI, 2738f)
        addActorWithConstraints(aPanelMenu) {
            startToStartOf   = this@addPanelMenu
            endToEndOf       = this@addPanelMenu
            bottomToBottomOf = this@addPanelMenu
        }

        aPanelMenu.y = -aPanelMenu.height
    }

    private fun Group.addDimImg() {
        aDimImg.apply {
            color.a = 0f
            disable()
        }

        addAndFillActor(aDimImg)
        aDimImg.setOnClickListener(null) { animHideMenu() }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animShowMenu() {
        aDimImg.apply {
            clearActions()
            enable()
            animShow(0.3f)
        }
        aPanelMenu.animShowMenu()
    }

    private fun animHideMenu() {
        aDimImg.apply {
            clearActions()
            disable()
            animHide(0.25f)
        }
        aPanelMenu.animHideMenu()
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