package com.lewydo.idlemergecubes.game.actors.panel.goals

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.vfx.AHslImage
import com.lewydo.idlemergecubes.game.model.GoalsModel
import com.lewydo.idlemergecubes.game.systems.goals.Goal
import com.lewydo.idlemergecubes.game.systems.goals.GoalProgress
import com.lewydo.idlemergecubes.game.utils.CubeColorSystem
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.enable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  APanelGoals — панель між TopPanel і Grid
//
//  Показує поточну задачу: тип, номер, таймер (Timed), нагороду,
//  прогрес та оверлей при завершенні/провалі.
//
//  Layout (1905 × 420):
//
//   ┌──────────────────────────────────────────────────────────────┐
//   │ [SIMPLE] #5                          [⏱ 30s]  [🌟 300]      │
//   │ ─────────────────────────────────────────────────────────── │
//   │  Simple:   [Cube] Reach this cube level        5/6          │
//   │            [=============-----------]                       │
//   │  Combined: Place on the board:                              │
//   │            [4] 1/1✓  [5] 0/4  [2] 0/2                      │
//   └──────────────────────────────────────────────────────────────┘
// ═════════════════════════════════════════════════════════════════════════════

class APanelGoals(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ── Розміри ───────────────────────────────────────────────────────────────

    private val PANEL_W = 1905f
    private val PANEL_H = 420f

    private val HEADER_Y     = 320f
    private val HEADER_H     = 88f
    private val DIVIDER_Y    = 310f
    private val BODY_BOTTOM  = 0f
    private val BODY_H       = 300f

    private val BAR_H        = 44f
    private val BAR_X        = 240f
    private val BAR_W        = PANEL_W - BAR_X - 42f

    private val ITEM_W       = 430f
    private val ITEM_H       = 180f
    private val ITEM_Y       = 60f
    private val ITEM_GAP     = 16f

    // ── Font ──────────────────────────────────────────────────────────────────

    private val fBadge  = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(50).setBorder(2f, Color.BLACK)
    private val fIndex  = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "#").setSize(50)
    private val fText   = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(46)
    private val fReward = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "+").setSize(58).setShadow(4, 4, Color.BLACK)
    private val fTimer  = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "s").setSize(52)
    private val fResult = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(78).setBorder(3f, Color.BLACK)

    // ── Header actors ─────────────────────────────────────────────────────────

    private val aBadgeBg   = Image(screen.drawerUtil.getTexture(GameColor.green_98FF68))
    private val aBadgeLbl  = Label("SIMPLE", FontFactory.create(screen, fBadge,  screen.fontGenerator_Nunito_Black, Color.WHITE))
    private val aIndexLbl  = Label("#1",     FontFactory.create(screen, fIndex,  screen.fontGenerator_Nunito_SemiBold, Color.valueOf("999999")))
    private val aTimerBg   = Image(screen.drawerUtil.getTexture(GameColor.purple_350080))
    private val aTimerLbl  = Label("30s",    FontFactory.create(screen, fTimer,  screen.fontGenerator_Nunito_Bold, Color.WHITE))
    private val aCoinImg   = Image(gdxGame.assetsAll.coin)
    private val aRewardLbl = Label("0",      FontFactory.create(screen, fReward, screen.fontGenerator_Nunito_Black, GameColor.yellow_FFF858))

    // ── Body: Simple ──────────────────────────────────────────────────────────

    private val aSimpleBody    = ATmpGroup(screen)
    private val aSimpleCubeImg = AHslImage(screen, gdxGame.assetsAll.cube)
    private val aSimpleLvlLbl  = Label("", FontFactory.create(screen, fIndex,  screen.fontGenerator_Nunito_Black, Color.WHITE))
    private val aSimpleDescLbl = Label("Reach this cube level", FontFactory.create(screen, fText, screen.fontGenerator_Nunito_Regular, Color.valueOf("BBBBBB")))
    private val aSimpleCountLbl = Label("", FontFactory.create(screen, fText,  screen.fontGenerator_Nunito_Bold, Color.WHITE))
    private val aSimpleBarBg   = Image(screen.drawerUtil.getTexture(GameColor.purple_350080))
    private val aSimpleBarFill = Image(screen.drawerUtil.getTexture(GameColor.green_98FF68))

    // ── Body: Combined/Timed ──────────────────────────────────────────────────

    private val aCombinedBody    = ATmpGroup(screen)
    private val aCombinedDescLbl = Label("Place on the board:", FontFactory.create(screen, fText, screen.fontGenerator_Nunito_Regular, Color.valueOf("BBBBBB")))
    private val aReqItems        = List(4) { AGoalRequirementItem(screen) }

    // ── Result overlay ────────────────────────────────────────────────────────

    private val aResultGroup   = ATmpGroup(screen)
    private val aResultBg      = Image(screen.drawerUtil.getTexture(GameColor.green_98FF68))
    private val aResultIconOk  = Image(gdxGame.assetsAll.coin)
    private val aResultIconX   = Image(gdxGame.assetsAll.bag_coins)
    private val aResultLbl     = Label("", FontFactory.create(screen, fResult, screen.fontGenerator_Nunito_Black, Color.WHITE))

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        addBackground()
        addHeader()
        addDivider()
        addBodySimple()
        addBodyCombined()
        addResultOverlay()

        // Ховаємо тіла до першого goal
        aSimpleBody.isVisible   = false
        aCombinedBody.isVisible = false
        aResultGroup.isVisible  = false
        aResultGroup.disable()

        collectGoal()
        collectProgress()
        collectTimer()
        collectGoalState()
        collectGoalCounter()
    }

    // ── Add actors ────────────────────────────────────────────────────────────

    private fun addBackground() {
        addAndFillActor(Image(gdxGame.assetsAll.panel_coin))
    }

    private fun addHeader() {
        // Badge pill
        addActor(aBadgeBg)
        aBadgeBg.setBounds(36f, HEADER_Y, 290f, HEADER_H)

        addActor(aBadgeLbl)
        aBadgeLbl.setBounds(50f, HEADER_Y + 6f, 262f, HEADER_H - 12f)
        aBadgeLbl.setAlignment(Align.center)

        // Index "#N"
        addActor(aIndexLbl)
        aIndexLbl.setPosition(344f, HEADER_Y + 12f)

        // Timer pill (hidden by default)
        addActor(aTimerBg)
        aTimerBg.setBounds(PANEL_W - 580f, HEADER_Y, 265f, HEADER_H)
        aTimerBg.isVisible = false

        addActor(aTimerLbl)
        aTimerLbl.setBounds(PANEL_W - 570f, HEADER_Y + 8f, 245f, HEADER_H - 16f)
        aTimerLbl.setAlignment(Align.center)
        aTimerLbl.isVisible = false

        // Reward
        addActor(aCoinImg)
        aCoinImg.setBounds(PANEL_W - 288f, HEADER_Y + 2f, 84f, 84f)

        addActor(aRewardLbl)
        aRewardLbl.setBounds(PANEL_W - 196f, HEADER_Y + 8f, 170f, HEADER_H - 16f)
    }

    private fun addDivider() {
        val div = Image(screen.drawerUtil.getTexture(GameColor.white_55))
        addActor(div)
        div.setBounds(36f, DIVIDER_Y, PANEL_W - 72f, 2f)
        div.color.a = 0.25f
    }

    private fun addBodySimple() {
        addActor(aSimpleBody)
        aSimpleBody.setBounds(0f, BODY_BOTTOM, PANEL_W, BODY_H)

        // Cube preview
        aSimpleBody.addActor(aSimpleCubeImg)
        aSimpleCubeImg.setBounds(36f, 90f, 165f, 165f)

        aSimpleBody.addActor(aSimpleLvlLbl)
        aSimpleLvlLbl.setBounds(52f, 118f, 133f, 110f)
        aSimpleLvlLbl.setAlignment(Align.center)

        // Description + counter
        aSimpleBody.addActor(aSimpleDescLbl)
        aSimpleDescLbl.setBounds(BAR_X, 195f, 1100f, 68f)

        aSimpleBody.addActor(aSimpleCountLbl)
        aSimpleCountLbl.setBounds(PANEL_W - 220f, 195f, 184f, 68f)
        aSimpleCountLbl.setAlignment(Align.right)

        // Progress bar
        aSimpleBody.addActor(aSimpleBarBg)
        aSimpleBarBg.setBounds(BAR_X, 115f, BAR_W, BAR_H)
        aSimpleBarBg.color.a = 0.3f

        aSimpleBody.addActor(aSimpleBarFill)
        aSimpleBarFill.setBounds(BAR_X, 115f, 0f, BAR_H)
    }

    private fun addBodyCombined() {
        addActor(aCombinedBody)
        aCombinedBody.setBounds(0f, BODY_BOTTOM, PANEL_W, BODY_H)

        aCombinedBody.addActor(aCombinedDescLbl)
        aCombinedDescLbl.setBounds(36f, 220f, 900f, 68f)

        aReqItems.forEachIndexed { i, item ->
            aCombinedBody.addActor(item)
            item.setBounds(36f + i * (ITEM_W + ITEM_GAP), ITEM_Y, ITEM_W, ITEM_H)
        }
    }

    private fun addResultOverlay() {
        addActor(aResultGroup)
        aResultGroup.setBounds(0f, 0f, PANEL_W, PANEL_H)

        aResultGroup.addActor(aResultBg)
        aResultBg.setBounds(0f, 0f, PANEL_W, PANEL_H)
        aResultBg.color.a = 0f

        // Icon — по центру зверху
        aResultGroup.addActor(aResultIconOk)
        aResultIconOk.setBounds(PANEL_W / 2f - 80f, PANEL_H / 2f + 10f, 160f, 160f)
        aResultIconOk.isVisible = false

        aResultGroup.addActor(aResultIconX)
        aResultIconX.setBounds(PANEL_W / 2f - 80f, PANEL_H / 2f + 10f, 160f, 160f)
        aResultIconX.isVisible = false

        aResultGroup.addActor(aResultLbl)
        aResultLbl.setBounds(0f, PANEL_H / 2f - 60f, PANEL_W, 90f)
        aResultLbl.setAlignment(Align.center)
    }

    // ── Collect ───────────────────────────────────────────────────────────────

    private fun collectGoal() {
        coroutine?.launch {
            gdxGame.modelGoals.currentGoalFlow.collect { goal ->
                goal ?: return@collect
                runGDX { rebuildForGoal(goal) }
            }
        }
    }

    private fun collectProgress() {
        coroutine?.launch {
            gdxGame.modelGoals.progressFlow.collect { progress ->
                progress ?: return@collect
                runGDX { applyProgress(progress) }
            }
        }
    }

    private fun collectTimer() {
        coroutine?.launch {
            gdxGame.modelGoals.timerFlow.collect { seconds ->
                runGDX {
                    aTimerLbl.setText("${seconds}s")
                    val isUrgent = seconds in 1..10
                    aTimerLbl.color = if (isUrgent) Color.RED    else Color.WHITE
                    aTimerBg.color.set(if (isUrgent) Color.valueOf("880000") else Color.valueOf("330077"))
                    aTimerBg.color.a = 0.85f
                    if (isUrgent) animTimerPulse()
                }
            }
        }
    }

    private fun collectGoalState() {
        coroutine?.launch {
            gdxGame.modelGoals.stateFlow.collect { state ->
                runGDX { applyState(state) }
            }
        }
    }

    private fun collectGoalCounter() {
        coroutine?.launch {
            gdxGame.modelGoals.goalCounterFlow.collect { counter ->
                runGDX { aIndexLbl.setText("#$counter") }
            }
        }
    }

    // ── Rebuild ───────────────────────────────────────────────────────────────

    private fun rebuildForGoal(goal: Goal) {
        hideResultOverlay()

        // Нагорода
        aRewardLbl.setText(NumberFormatter.format(goal.reward))

        when (goal) {
            is Goal.Simple -> {
                setBadge("SIMPLE", Color.valueOf("1E9E50"))
                showTimer(false)
                aSimpleBody.isVisible   = true
                aCombinedBody.isVisible = false

                val color = CubeColorSystem.getCubeColor(goal.targetLevel)
                aSimpleCubeImg.setColorShader(color)
                aSimpleLvlLbl.setText(goal.targetLevel.toString())
                aSimpleBarFill.setSize(0f, BAR_H)
            }
            is Goal.Combined -> {
                setBadge("COMBINED", Color.valueOf("2244CC"))
                showTimer(false)
                aSimpleBody.isVisible   = false
                aCombinedBody.isVisible = true
                aCombinedDescLbl.setText("Place on the board:")
                rebuildReqItems(goal.requirements)
            }
            is Goal.Timed -> {
                setBadge("TIMED", Color.valueOf("BB2233"))
                showTimer(true)
                aSimpleBody.isVisible   = false
                aCombinedBody.isVisible = true
                aCombinedDescLbl.setText("Place on the board in Time:")
                rebuildReqItems(goal.requirements)
            }
        }
    }

    private fun setBadge(text: String, color: Color) {
        aBadgeLbl.setText(text)
        aBadgeBg.color.set(color)
    }

    private fun showTimer(visible: Boolean) {
        aTimerLbl.isVisible = visible
        aTimerBg.isVisible  = visible
    }

    private fun rebuildReqItems(reqs: List<Goal.Combined.Requirement>) {
        aReqItems.forEachIndexed { i, item ->
            val req = reqs.getOrNull(i)
            item.isVisible = req != null
            if (req != null) item.setRequirement(req.level, req.count)
        }
    }

    // ── Progress ──────────────────────────────────────────────────────────────

    private fun applyProgress(progress: GoalProgress) {
        when (progress) {
            is GoalProgress.Simple -> {
                aSimpleCountLbl.setText("${progress.current}/${progress.target}")
                val fillW = BAR_W * progress.progress
                aSimpleBarFill.setSize(fillW, BAR_H)
            }
            is GoalProgress.Combined -> {
                progress.items.forEachIndexed { i, item ->
                    aReqItems.getOrNull(i)?.updateProgress(item.current, item.required)
                }
            }
        }
    }

    // ── Goal state ────────────────────────────────────────────────────────────

    private fun applyState(state: GoalsModel.State) {
        when (state) {
            GoalsModel.State.ACTIVE    -> { /* нічого — прогрес оновлює collectProgress */ }
            GoalsModel.State.COMPLETED -> showCompleted()
            GoalsModel.State.FAILED    -> showFailed()
        }
    }

    private fun showCompleted() {
        aResultBg.color.set(Color.valueOf("158040"))
        aResultBg.color.a = 0f
        aResultIconOk.isVisible = true
        aResultIconX.isVisible  = false
        aResultLbl.setText("+${NumberFormatter.format(gdxGame.modelGoals.currentGoal?.reward ?: 0)}")
        aResultLbl.color = Color.WHITE
        animShowResult()
    }

    private fun showFailed() {
        aResultBg.color.set(Color.valueOf("8B1020"))
        aResultBg.color.a = 0f
        aResultIconOk.isVisible = false
        aResultIconX.isVisible  = true
        aResultLbl.setText("Failed!")
        aResultLbl.color = Color.WHITE
        animShowResult()
    }

    private fun hideResultOverlay() {
        aResultGroup.clearActions()
        aResultGroup.isVisible = false
        aResultGroup.disable()
        aResultBg.color.a = 0f
        aResultIconOk.isVisible = false
        aResultIconX.isVisible  = false
        aResultIconOk.setScale(1f)
        aResultIconX.setScale(1f)
    }

    // ── Animations ────────────────────────────────────────────────────────────

    private fun animShowResult() {
        aResultGroup.isVisible = true
        aResultGroup.enable()

        // Фон
        aResultBg.clearActions()
        aResultBg.addAction(Actions.fadeIn(0.3f))

        // Іконка — з'являється з bounce
        val icon = if (aResultIconOk.isVisible) aResultIconOk else aResultIconX
        icon.setScale(0f)
        icon.clearActions()
        icon.addAction(Actions.sequence(
            Actions.delay(0.1f),
            Actions.scaleTo(1.25f, 1.25f, 0.22f, Interpolation.swingOut),
            Actions.scaleTo(1.0f,  1.0f,  0.14f, Interpolation.sineOut),
        ))
    }

    private var timerPulseRunning = false
    private fun animTimerPulse() {
        if (timerPulseRunning) return
        timerPulseRunning = true
        aTimerBg.clearActions()
        aTimerBg.addAction(Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, 0.12f, Interpolation.sineOut),
            Actions.scaleTo(1.0f,  1.0f,  0.12f, Interpolation.sineOut),
            Actions.run { timerPulseRunning = false }
        ))
    }
}