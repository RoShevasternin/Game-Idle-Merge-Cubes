package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.ABuyButton
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogClearGrid
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogLevelUp
import com.lewydo.idlemergecubes.game.actors.dialog.ADialogOfflineReward
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.panel.APanelTop
import com.lewydo.idlemergecubes.game.actors.panelGrid.APanelGrid
import com.lewydo.idlemergecubes.game.actors.panelIdle.APanelIdle
import com.lewydo.idlemergecubes.game.actors.panelMenu.APanelMenu
import com.lewydo.idlemergecubes.game.actors.tutorial.ATutorial
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
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.launch

class GameScreen: AdvancedScreen() {

    companion object {
        private var IS_CHECK_OFFLINE_REWARD = true
    }

//    private val font  = fontGenerator_Nunito_Black.generateFont(FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "FPS").setSize(100))
//    private val label = Label("FPS", Label.LabelStyle(font, Color.WHITE))


    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  = APanelTop(this)
    private val aPanelGrid = APanelGrid(this)
    private val aPanelIdle = APanelIdle(this)

    private val aBuyBtn = ABuyButton(this)

    private val aDimImg    = Image(drawerUtil.getTexture(GameColor.black_55))
    //private val aBackBlur  = ABlurBack(this@GameScreen)

    private val aPanelMenu = APanelMenu(this)

    private val aDialogClearGrid     = ADialogClearGrid(this)
    private val aDialogOfflineReward = ADialogOfflineReward(this)
    private val aDialogLevelUp       = ADialogLevelUp(this)

    // поле — lazy бо не треба якщо вже пройдений
    private val aTutorial by lazy { ATutorial(this) }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private var currentState = StateDim.NONE

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.3f
    private val timeHide = 0.25f

    private var isClosableDim = true

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()

//        stageUI.root.addActorWithConstraints(label) {
//            topToTopOf = stageUI.root
//            endToEndOf = stageUI.root
//            marginTop  = 244f
//            marginEnd  = 550f
//        }
    }

//    override fun render(delta: Float) {
//        super.render(delta)
//        label.setText("${Gdx.graphics.framesPerSecond} FPS")
//    }

    override fun Group.addActorsOnStageUI() {
        color.a = 0f

        addPanelTop()
        addPanelGame()
        addPanelIdle()
        addBuyBtn()

        aPanelTop.toFront()
        addTutorial()

        addDimImg()
        addPanelMenu()

        addDialogClearGrid()
        addDialogLevelUp()
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


//        var isTestVisible = true
//        aPanelTop.setOnClickListener {
//            if (isTestVisible) {
//                animHideScreen()
//            } else {
//                animShowScreen()
//            }
//            isTestVisible = !isTestVisible
//        }
    }

    private fun Group.addPanelGame() {
        aPanelGrid.setSize(1905f, 1905f)
        addActorWithConstraints(aPanelGrid) {
            startToStartOf = this@addPanelGame
            endToEndOf     = this@addPanelGame
            topToBottomOf  = aPanelTop

            marginTop = 238f
        }
    }

    private fun Group.addPanelIdle() {
        aPanelIdle.setSize(1905f, 428f)
        addActorWithConstraints(aPanelIdle) {
            startToStartOf = this@addPanelIdle
            endToEndOf     = this@addPanelIdle
            topToBottomOf  = aPanelGrid

            marginTop = 151f
        }
    }

    private fun Group.addBuyBtn() {
        aBuyBtn.setSize(1905f, 386f)
        addActorWithConstraints(aBuyBtn) {
            startToStartOf   = this@addBuyBtn
            endToEndOf       = this@addBuyBtn
            bottomToBottomOf = this@addBuyBtn

            marginBottom = 200f
        }

        aBuyBtn.onClick = {
            aPanelGrid.buyCube()
            gdxGame.tutorialManager.onBuyDone()
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

        aPanelMenu.blockClose = { setState(StateDim.NONE) }
        aPanelMenu.blockClearGrid = { setState(StateDim.DIALOG_CLEAR_GRID) }
    }

    private fun Group.addDimImg() {
        //aBackBlur.animHideAndDisable()
        //aBackBlur.isStaticEffect = true
        //aBackBlur.radiusBlur     = 0f
        //addAndFillActor(aBackBlur)

        aDimImg.animHideAndDisable()
        addAndFillActor(aDimImg)

        aDimImg.setOnClickListener(null) { if (isClosableDim) setState(StateDim.NONE) }
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

        if (IS_CHECK_OFFLINE_REWARD) {
            IS_CHECK_OFFLINE_REWARD = false
            checkAvailableOfflineReward()
        }
    }

    private fun Group.addDialogLevelUp() {
        aDialogLevelUp.animHideAndDisable()
        aDialogLevelUp.setSize(1908f, 2333f)
        addActorAligned(aDialogLevelUp, AlignH.CENTER, AlignV.CENTER)

        collectLevelUp()
    }

    // новий метод:
    private fun Group.addTutorial() {
        if (gdxGame.tutorialManager.isDone) return

        addAndFillActor(aTutorial)

        animDelay(0.5f) {
            val pos = aBuyBtn.localToStageCoordinates(Vector2(aBuyBtn.width * 0.85f, aBuyBtn.height * 0.4f))
            GlobalStagePositions.register(GlobalStagePositions.Position.BUY_BTN, pos.x, pos.y)
            aTutorial.start()
        }

    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animShowDim() {
        aDimImg.clearActions()
        aDimImg.animShowAndEnable(timeShow)

        //aBackBlur.radiusBlur = 7f
        //aBackBlur.captureOnce()
        //aBackBlur.animShow(timeShow)
    }

    private fun animHideDim() {
        aDimImg.clearActions()
        aDimImg.animHideAndDisable(timeHide)

        //aBackBlur.animHide(timeHide)
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
            StateDim.DIALOG_OFFLINE_REWARD -> {
                aDialogOfflineReward.animHideAndDisable(timeHide) {
                    aDialogOfflineReward.stopEffect()
                }
            }
            StateDim.DIALOG_LEVEL_UP       -> {
                aDialogLevelUp.animHideAndDisable(timeHide) {
                    aDialogLevelUp.stopEffect()
                }
            }
            StateDim.NONE -> {}
        }

        currentState = newState

        // enter new state
        when (newState) {
            StateDim.MENU                  -> {
                isClosableDim = true
                animShowDim()
                aPanelMenu.animShowMenu(timeShow + 0.05f)
            }
            StateDim.DIALOG_CLEAR_GRID     -> aDialogClearGrid.animShowAndEnable(timeShow)
            StateDim.DIALOG_OFFLINE_REWARD -> {
                isClosableDim = false
                animShowDim()
                aDialogOfflineReward.animShowAndEnable(timeShow) {
                    aDialogOfflineReward.startEffect()
                }
            }
            StateDim.DIALOG_LEVEL_UP -> {
                isClosableDim = false
                animShowDim()

                gdxGame.soundUtil.apply { play(LEVEL_UP) }

                aDialogLevelUp.animShowAndEnable(timeShow) {
                    aDialogLevelUp.startEffect()
                }
            }
            StateDim.NONE -> animHideDim()
        }
    }

    // ------------------------------------------------------------------------
    // checkAvailableOfflineReward
    // ------------------------------------------------------------------------

    private fun checkAvailableOfflineReward() {
        val result = gdxGame.modelOfflineReward.calculate()
        if (result is OfflineRewardModel.OfflineResult.Reward) {
            aDialogOfflineReward.setReward(result.coins)
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
    // collectLevelUp
    // ------------------------------------------------------------------------

    private fun collectLevelUp() {
        // Пропускаємо перший emit (початковий рівень при запуску)
        var previousLevel = gdxGame.modelPlayer.currentLevel

        coroutine?.launch {
            gdxGame.modelPlayer.levelFlow.collect { newLevel ->
                if (newLevel > previousLevel) {
                    previousLevel = newLevel
                    val reward = gdxGame.modelLevelUp.calculateReward(newLevel)

                    runGDX {
                        aDialogLevelUp.setLevel(newLevel)
                        aDialogLevelUp.setReward(reward)

                        aDialogLevelUp.onCollect = {
                            gdxGame.modelLevelUp.collect(newLevel)
                            setState(StateDim.NONE)
                        }
                        aDialogLevelUp.onCollectX2 = {
                            gdxGame.modelLevelUp.collectX2(newLevel)
                            setState(StateDim.NONE)
                        }

                        setState(StateDim.DIALOG_LEVEL_UP)
                    }
                }
            }
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
        DIALOG_LEVEL_UP,
    }

}