package com.lewydo.rozval.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.lewydo.rozval.game.actors.autoLayout.ATableGroup
import com.lewydo.rozval.game.actors.autoLayout.AutoLayout
import com.lewydo.rozval.game.actors.button.location.button.AAvailableLevelButton
import com.lewydo.rozval.game.actors.button.location.button.ALockStarKeyLevelButton
import com.lewydo.rozval.game.actors.button.location.button.ALockStarLevelButton
import com.lewydo.rozval.game.actors.button.location.button.AbstractLevelButton
import com.lewydo.rozval.game.actors.button.location.separator.ALockSeparatorButton
import com.lewydo.rozval.game.actors.button.location.separator.AbstractSeparatorButton
import com.lewydo.rozval.game.actors.mainPanel.MenuMainPanel
import com.lewydo.rozval.game.utils.Block
import com.lewydo.rozval.game.utils.TIME_ANIM_SCREEN
import com.lewydo.rozval.game.utils.actor.HAlign
import com.lewydo.rozval.game.utils.actor.VAlign
import com.lewydo.rozval.game.utils.actor.addActorAligned
import com.lewydo.rozval.game.utils.actor.animDelay
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedStage
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.game.utils.manager.LevelButtonManager
import com.lewydo.rozval.game.utils.manager.LocationManager
import kotlinx.coroutines.launch
import kotlin.collections.get
import kotlin.invoke

class MenuScreen: AdvancedScreen() {

    companion object {
        var LVL_CLICK = 1 // 0
            private set
    }

    val mainPanel          = MenuMainPanel(this)
    private val tableGroup = getTableGroup()

    // Field
    private val locationManager = LocationManager(this)
    private val locationsUtil   = gdxGame.ds_Location
    private val keyUtil         = gdxGame.ds_key

    var availableLevelBtnBlock: (Int) -> Unit = {}

    override fun Group.addActorsOnStageUI() {
        mainLogic()

        stageUI.root.color.a = 0f

        addMainPanel()
        addTableGroup()

        animShow {
            coroutine?.launch { addLevels() }
        }
    }

    override fun animHide(blockEnd: Block) {
        stageUI.root.children.onEach { it.clearActions() }

        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShow(blockEnd: Block) {
        stageUI.root.children.onEach { it.clearActions() }

        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // Init ------------------------------------------------------------------------

    private fun getTableGroup(): ATableGroup {
        return ATableGroup(this, startGapV = 50f, endGapV = 50f, gapV = 50f, gapH = 85f,
            alignmentVH = AutoLayout.AlignmentHorizontal.CENTER,
            alignmentVV = AutoLayout.AlignmentVertical.TOP,
            alignmentHH = AutoLayout.AlignmentHorizontal.CENTER,
            alignmentHV = AutoLayout.AlignmentVertical.CENTER,
        )
    }

    // Actors ------------------------------------------------------------------------

    private fun Group.addMainPanel() {
        mainPanel.setSize(155f, 1080f)
        addActorAligned(mainPanel, HAlign.END, VAlign.CENTER)
    }

    private fun Group.addTableGroup() {
        tableGroup.setSize(1765f, 1080f)
        addActorAligned(tableGroup, HAlign.CENTER, VAlign.CENTER)
    }

    private suspend fun addLevels() {
        LocationManager.LocationType.entries.onEach { locationType ->
            locationManager.locationType = locationType
            locationManager.getSeparatorButton().also { separatorBtn ->
                separatorBtn.setOnClickBlock(locationType)
                tableGroup.addToTable(separatorBtn)
            }
            locationManager.getButtonList().onEachIndexed { index, btn ->
                btn.setOnClickBlock(locationType, index)
                tableGroup.addToTable(btn)
            }
        }
    }

    // Logic ------------------------------------------------------------------------

    fun mainLogic() {
        availableLevelBtnBlock = {
            LVL_CLICK = it
            animHide { gdxGame.navigationManager.navigate(GameScreen::class.java.name, MenuScreen::class.java.name) }
        }

        mainPanel.exitBtnBlock = { animHide { gdxGame.navigationManager.exit() } }
        mainPanel.getKeysBtnBlock = { /*hideScreen { game.navigationManager.exit() }*/ }
    }

    private fun AbstractSeparatorButton.setOnClickBlock(locationType: LocationManager.LocationType) {
        if (this is ALockSeparatorButton) {
            onClickBlock = {
                changeToAvailableSeparatorButton()
                (locationManager.levelButtonManager.mapLocationLevelButton[locationType]?.first() as? ALockStarLevelButton)?.changeToAvailableLevelButton()

                keyUtil.update { it - locationType.separatorNecessaryKeys }
                locationsUtil.update { locations ->
                    locations.apply { get(locationType.ordinal).apply {
                        isOpen = true
                        levels.first().isOpen = true
                    } }
                }

            }
        }
    }

    private fun AbstractLevelButton.setOnClickBlock(locationType: LocationManager.LocationType, indexBtn: Int) {
        when(this) {
            is AAvailableLevelButton -> {
                setOnClickBlock {
                    availableLevelBtnBlock.invoke(locationType.startLevel+indexBtn)
                }
            }
            is ALockStarKeyLevelButton -> {
                onClickBlock = {
                    changeToAvailableLevelButton()

                    keyUtil.update { it - LevelButtonManager.NECESSARY_KEY }
                    locationsUtil.update { locations ->
                        locations.apply { get(locationType.ordinal).apply {
                            levels[indexBtn].isOpen = true
                        } }
                    }

                }
            }
        }
    }

}