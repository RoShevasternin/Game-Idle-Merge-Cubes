package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.actors.button.ABuyButton
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogClearGrid
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogLevelUp
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogOfflineReward
import com.lewydo.idlemergecubes.game.actors.panel.goals.APanelGoals
import com.lewydo.idlemergecubes.game.actors.hint.ABuyHint
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.APanelMergeBonusWithGoals
import com.lewydo.idlemergecubes.game.actors.panel.APanelTop
import com.lewydo.idlemergecubes.game.actors.panel.grid.APanelGrid
import com.lewydo.idlemergecubes.game.actors.panel.menu.APanelMenu
import com.lewydo.idlemergecubes.game.actors.tutorial.ATutorial
import com.lewydo.idlemergecubes.game.model.OfflineRewardModel
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.WIDTH_UI
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animHideAndDisable
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.animShowAndEnable
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.overlay.OverlayManager
import com.lewydo.idlemergecubes.game.utils.runGDX
import com.lewydo.idlemergecubes.services.tiktok.TikTokManager
import com.lewydo.idlemergecubes.util.log
import com.tiktok.TikTokBusinessSdk
import com.tiktok.appevents.base.TTBaseEvent
import kotlinx.coroutines.launch

class GameScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Companion
    // ------------------------------------------------------------------------

    companion object {
        private var IS_CHECK_OFFLINE_REWARD = true
        private val IS_FPS_DEBUG = BuildConfig.DEBUG
    }

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------

    private enum class Overlay { MENU, CLEAR_GRID, OFFLINE_REWARD, LEVEL_UP }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.clearActions(); aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.clearActions(); aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop                 by lazy { APanelTop(this) }
    private val aPanelGrid                by lazy { APanelGrid(this) }
    private val aPanelMergeBonusWithGoals by lazy { APanelMergeBonusWithGoals(this) }
    private val aPanelGoals               by lazy { APanelGoals(this) }
    private val aBuyBtn                   by lazy { ABuyButton(this) }
    private val aBuyHint                  by lazy { ABuyHint(this) }
    private val aPanelMenu                by lazy { APanelMenu(this) }
    private val aDialogClearGrid          by lazy { ADialogClearGrid(this) }
    private val aDialogOfflineReward      by lazy { ADialogOfflineReward(this) }
    private val aDialogLevelUp            by lazy { ADialogLevelUp(this) }
    private val aTutorial                 by lazy { ATutorial(this) }  // lazy — не потрібен якщо туторіал пройдений

    private val aDimImg = Image(drawerUtil.getTexture(GameColor.black_55))

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------

    private val timeShow = 0.3f
    private val timeHide = 0.25f

    // ------------------------------------------------------------------------
    // Debug
    // ------------------------------------------------------------------------

    private val fpsFont  = fontGenerator_Nunito_Black.generateFont(FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "FPS").setSize(100))
    private val fpsLabel = Label("FPS", Label.LabelStyle(fpsFont, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun show() {
        stageUI.root.color.a = 0f
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()

        if (IS_FPS_DEBUG) {
            rootConstraintLayout.add(fpsLabel) {
                endToEnd(margin = 550f)
                topToTop(margin = 244f)
            }
        }

        animShowScreen()
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (IS_FPS_DEBUG) fpsLabel.setText("${Gdx.graphics.framesPerSecond} FPS")
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelGame()
        addPanelMergeBonusWithGoals()
        addBuyBtn()
        addBuyHint()

        aPanelTop.toFront()

        addTutorial()
        addDimImg()
        addPanelMenu()
        addDialogClearGrid()
        addDialogLevelUp()
        addDialogOfflineReward()
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

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(2160f, 467f)
        add(aPanelTop) {
            centerX()
            topToTop()
        }
        aPanelTop.onClickSettingsBtn = { overlayManager.show(Overlay.MENU) }
    }

    private fun AConstraintLayout.addPanelGame() {
        aPanelGrid.setSize(1905f, 1905f)
        add(aPanelGrid) {
            centerX()
            topToBottom(aPanelTop, 238f)
        }
        aPanelGrid.onMergeExecuted = { isEnchanted ->
            gdxGame.modelMergeBonus.onMerge(isEnchanted)
        }
    }

    private fun AConstraintLayout.addPanelMergeBonusWithGoals() {
        aPanelMergeBonusWithGoals.setSize(1905f, 895f)
        add(aPanelMergeBonusWithGoals) {
            centerX()
            topToBottom(aPanelGrid, 34f)
        }

        //aPanelGoals.setSize(1905f, 420f)
        //add(aPanelGoals) {
        //    centerX()
        //    topToBottom(aPanelTop, 32f)
        //}
    }

    private fun AConstraintLayout.addBuyBtn() {
        aBuyBtn.setSize(1905f, 386f)
        add(aBuyBtn) {
            centerX()
            bottomToBottom(margin = 200f)
        }

        //var t = true

        aBuyBtn.onClick = {
            aPanelGrid.buyCube()
            gdxGame.tutorialManager.onBuyDone()

            //TikTokManager.sendTestEvent()

//            if (t) {
//                t = false
//                gdxGame.activity.adManager.banner.show()
//            } else {
//                t = true
//                gdxGame.activity.adManager.banner.hide()
//            }
        }
    }

    private fun AConstraintLayout.addBuyHint() {
        aBuyHint.setSize(1864f, 61f)
        add(aBuyHint) {
            startToStart(aBuyBtn)
            endToEnd(aBuyBtn)
            topToBottom(aBuyBtn, margin = 16f)
        }
    }

    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) { fillParent() }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPanelMenu() {
        aPanelMenu.disable()
        aPanelMenu.setSize(WIDTH_UI, 2738f)
        add(aPanelMenu) { centerX() }
        aPanelMenu.y = -aPanelMenu.height

        aPanelMenu.blockClose     = { overlayManager.close() }
        aPanelMenu.blockClearGrid = { overlayManager.show(Overlay.CLEAR_GRID) }

        overlayManager.register(Overlay.MENU, OverlayManager.Config(
            showDim    = true,
            isClosable = true,
            onShow     = { aPanelMenu.animShowMenu(timeShow + 0.05f) },
            onHide     = { aPanelMenu.animHideMenu(timeHide + 0.03f) },
        ))
    }

    private fun AConstraintLayout.addDialogClearGrid() {
        aDialogClearGrid.animHideAndDisable()
        aDialogClearGrid.setSize(1433f, 635f)
        add(aDialogClearGrid) { center() }

        aDialogClearGrid.blockYes = { aPanelGrid.resetGrid(); overlayManager.close() }
        aDialogClearGrid.blockNo  = { overlayManager.close() }

        overlayManager.register(Overlay.CLEAR_GRID, OverlayManager.Config(
            showDim    = true,
            isClosable = false,
            onShow     = { aDialogClearGrid.animShowAndEnable(timeShow) },
            onHide     = { aDialogClearGrid.animHideAndDisable(timeHide) },
        ))
    }

    private fun AConstraintLayout.addDialogOfflineReward() {
        aDialogOfflineReward.animHideAndDisable()
        aDialogOfflineReward.setSize(1908f, 2333f)
        add(aDialogOfflineReward) { center() }

        overlayManager.register(Overlay.OFFLINE_REWARD, OverlayManager.Config(
            showDim    = true,
            isClosable = false,
            onShow     = { aDialogOfflineReward.animShowAndEnable(timeShow) { aDialogOfflineReward.startEffect() } },
            onHide     = { aDialogOfflineReward.animHideAndDisable(timeHide) { aDialogOfflineReward.stopEffect() } },
        ))

        if (IS_CHECK_OFFLINE_REWARD) {
            IS_CHECK_OFFLINE_REWARD = false
            checkAvailableOfflineReward()
        }
    }

    private fun AConstraintLayout.addDialogLevelUp() {
        aDialogLevelUp.animHideAndDisable()
        aDialogLevelUp.setSize(1908f, 2333f)
        add(aDialogLevelUp) { center() }

        overlayManager.register(Overlay.LEVEL_UP, OverlayManager.Config(
            showDim    = true,
            isClosable = false,
            onShow     = {
                gdxGame.soundUtil.apply { play(LEVEL_UP) }
                aDialogLevelUp.animShowAndEnable(timeShow) { aDialogLevelUp.startEffect() }
            },
            onHide     = { aDialogLevelUp.animHideAndDisable(timeHide) { aDialogLevelUp.stopEffect() } },
        ))

        collectLevelUp()
    }

    private fun Group.addTutorial() {
        if (gdxGame.tutorialManager.isDone) return
        addAndFillActor(aTutorial)
        animDelay(0.5f) {
            GlobalStagePositions.register(
                key      = GlobalStagePositions.Key.BUY_BTN,
                actor    = aBuyBtn,
                offsetX  = aBuyBtn.width * 0.85f,
                offsetY  = aBuyBtn.height * 0.4f,
            )

            aTutorial.start()
        }
    }

    // ------------------------------------------------------------------------
    // Offline Reward
    // ------------------------------------------------------------------------

    private fun checkAvailableOfflineReward() {
        val result = gdxGame.modelOfflineReward.calculate()
        if (result !is OfflineRewardModel.OfflineResult.Reward) return

        aDialogOfflineReward.setReward(result.coins)
        aDialogOfflineReward.setDuration(result.duration.toDisplayString())

        aDialogOfflineReward.onCollect = {
            gdxGame.analytics.collectOffline(result.coins)
            gdxGame.modelOfflineReward.collect(result)
            overlayManager.close()
        }
        aDialogOfflineReward.onCollectX2 = {
            gdxGame.activity.adManager.rewarded.show(
                onEarned = {
                    gdxGame.analytics.collectOfflineX2(result.coins)
                    gdxGame.analytics.adWatched("offline_x2")
                    gdxGame.modelOfflineReward.collectX2(result)
                    runGDX { overlayManager.close() }
                },
                onDismissed = {},
                onFailed = {
                    gdxGame.analytics.collectOffline(result.coins)
                    gdxGame.modelOfflineReward.collect(result)
                    runGDX { overlayManager.close() }
                }
            )
        }

        overlayManager.show(Overlay.OFFLINE_REWARD)
    }

    // ------------------------------------------------------------------------
    // Level Up
    // ------------------------------------------------------------------------

    private fun collectLevelUp() {
        var previousLevel = gdxGame.modelPlayer.currentLevel

        coroutine?.launch {
            gdxGame.modelPlayer.levelFlow.collect { newLevel ->
                if (newLevel <= previousLevel) return@collect
                previousLevel = newLevel

                val reward = gdxGame.modelLevelUp.calculateReward(newLevel)

                runGDX {
                    aDialogLevelUp.setLevel(newLevel)
                    aDialogLevelUp.setReward(reward)
                    gdxGame.analytics.levelUp(newLevel)

                    aDialogLevelUp.onCollect = {
                        gdxGame.analytics.collectNewLevel(reward)
                        gdxGame.modelLevelUp.collect(newLevel)
                        overlayManager.close()
                    }
                    aDialogLevelUp.onCollectX2 = {
                        gdxGame.activity.adManager.rewarded.show(
                            onEarned = {
                                gdxGame.analytics.collectNewLevelX2(reward * 2)
                                gdxGame.analytics.adWatched("level_up_x2")
                                gdxGame.modelLevelUp.collectX2(newLevel)
                                runGDX { overlayManager.close() }
                            },
                            onDismissed = {},
                            onFailed = {
                                gdxGame.analytics.collectNewLevel(reward)
                                gdxGame.modelLevelUp.collect(newLevel)
                                runGDX { overlayManager.close() }
                            }
                        )
                    }

                    overlayManager.show(Overlay.LEVEL_UP)
                }
            }
        }
    }

}