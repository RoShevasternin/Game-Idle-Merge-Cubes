package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.ABuyButton
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogClearGrid
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogOfflineReward
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.panel.APanelTop
import com.lewydo.idlemergecubes.game.actors.panelGrid.APanelGrid
import com.lewydo.idlemergecubes.game.actors.panelIdle.APanelIdle
import com.lewydo.idlemergecubes.game.actors.panelMenu.APanelMenu
import com.lewydo.idlemergecubes.game.model.OfflineRewardModel
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.WIDTH_UI
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animHideAndDisable
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.animShowAndEnable
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class GameScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)
    private val aPanelGrid = APanelGrid(this)
    private val aPanelIdle = APanelIdle(this)

    private val aBuyBtn = ABuyButton(this)

    private val aDimImg = Image(drawerUtil.getTexture(GameColor.black_55))
    private val aPanelMenu = APanelMenu(this)

    private val aDialogClearGrid     = ADialogClearGrid(this)
    private val aDialogOfflineReward = ADialogOfflineReward(this)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private var currentState = StateDim.NONE

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.3f
    private val timeHide = 0.25f

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

        addDialogClearGrid()
        addDialogOfflineReward()

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
        addActorAligned(aPanelTop, AlignH.CENTER, AlignV.TOP)

        aPanelTop.onClickSettingsBtn = { setState(StateDim.MENU) }


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
            startToStartOf = this@addPanelGame
            endToEndOf = this@addPanelGame
            topToBottomOf = aPanelTop

            marginTop = 238f
        }
    }

    private fun Group.addPanelIdle() {
        aPanelIdle.setSize(1905f, 428f)
        addActorWithConstraints(aPanelIdle) {
            startToStartOf = this@addPanelIdle
            endToEndOf = this@addPanelIdle
            topToBottomOf = aPanelGrid

            marginTop = 151f
        }
    }

    private fun Group.addBuyBtn() {
        aBuyBtn.setSize(1107f, 345f)
        addActorWithConstraints(aBuyBtn) {
            startToStartOf = this@addBuyBtn
            endToEndOf = this@addBuyBtn
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
            startToStartOf = this@addPanelMenu
            endToEndOf = this@addPanelMenu
            bottomToBottomOf = this@addPanelMenu
        }

        aPanelMenu.y = -aPanelMenu.height

        aPanelMenu.blockClose = { setState(StateDim.NONE) }
        aPanelMenu.blockClearGrid = { setState(StateDim.DIALOG_CLEAR_GRID) }
    }

    private fun Group.addDimImg() {
        aDimImg.apply {
            color.a = 0f
            disable()
        }

        addAndFillActor(aDimImg)
        aDimImg.setOnClickListener(null) { setState(StateDim.NONE) }
    }

    private fun Group.addDialogClearGrid() {
        aDialogClearGrid.animHideAndDisable()
        aDialogClearGrid.setSize(1433f, 635f)
        addActorAligned(aDialogClearGrid, AlignH.CENTER, AlignV.CENTER)

        aDialogClearGrid.blockYes = {
            aPanelGrid.resetGrid()
            setState(StateDim.NONE)
        }
        aDialogClearGrid.blockNo = {
            setState(StateDim.NONE)
        }

    }

    private fun Group.addDialogOfflineReward() {
        aDialogOfflineReward.animHideAndDisable()
        aDialogOfflineReward.setSize(1908f, 2333f)
        addActorAligned(aDialogOfflineReward, AlignH.CENTER, AlignV.CENTER)

        checkAvailableOfflineReward()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animShowDim(isEnable: Boolean = true) {
        aDimImg.clearActions()
        if (isEnable) aDimImg.animShowAndEnable(timeShow) else aDimImg.animShow(timeShow)
    }

    private fun animHideDim() {
        aDimImg.clearActions()
        aDimImg.animHideAndDisable(0.25f)
    }

    // ------------------------------------------------------------------------
    // setState
    // ------------------------------------------------------------------------

    private fun setState(newState: StateDim) {
        if (currentState == newState) return

        // exit old state
        when (currentState) {
            StateDim.MENU                  -> aPanelMenu.animHideMenu(timeHide + 0.03f)
            StateDim.DIALOG_CLEAR_GRID     -> aDialogClearGrid.animHideAndDisable(timeHide)
            StateDim.DIALOG_OFFLINE_REWARD -> aDialogOfflineReward.animHideAndDisable(timeHide)
            StateDim.NONE -> {}
        }

        currentState = newState

        // enter new state
        when (newState) {
            StateDim.MENU                  -> {
                animShowDim()
                aPanelMenu.animShowMenu(timeShow + 0.05f)
            }
            StateDim.DIALOG_CLEAR_GRID     -> aDialogClearGrid.animShowAndEnable(timeShow)
            StateDim.DIALOG_OFFLINE_REWARD -> {
                animShowDim(false)
                aDialogOfflineReward.animShowAndEnable(timeShow) {
                    aDialogOfflineReward.startEffect()
                }
            }
            StateDim.NONE -> animHideDim()
        }
    }

    private fun checkAvailableOfflineReward() {
        val result = gdxGame.modelOfflineReward.calculate()
        if (result is OfflineRewardModel.OfflineResult.Reward) {
            aDialogOfflineReward.setReward(result.coins.toInt())
            aDialogOfflineReward.setDuration(result.duration.toDisplayString())

            aDialogOfflineReward.onCollect   = {
                gdxGame.modelOfflineReward.collect(result)
                setState(StateDim.NONE)
            }
            aDialogOfflineReward.onCollectX2 = {
                gdxGame.modelOfflineReward.collectX2(result)
                setState(StateDim.NONE)
            }

            setState(StateDim.DIALOG_OFFLINE_REWARD)
        }
    }

    // ------------------------------------------------------------------------
    // enum State
    // ------------------------------------------------------------------------
    private enum class StateDim {
        NONE,
        MENU,
        DIALOG_CLEAR_GRID,
        DIALOG_OFFLINE_REWARD,
    }

}