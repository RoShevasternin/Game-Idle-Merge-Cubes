package com.lewydo.idlemergecubes.game.state

import com.lewydo.idlemergecubes.game.data.GoalState
import com.lewydo.idlemergecubes.game.data.PlayerData
import kotlinx.coroutines.flow.MutableStateFlow

class GameState {

    // ── Flows ─────────────────────────────────────────────────────────────────

    val coinsFlow           = MutableStateFlow(0L)
    val xpFlow              = MutableStateFlow(0L)
    val gridFlow            = MutableStateFlow(List(0) { 0 })
    val mergeBonusCountFlow = MutableStateFlow(0)
    val mergeBonusGoalFlow  = MutableStateFlow(0)

    // ── Misc ──────────────────────────────────────────────────────────────────

    var lastLoginTime: Long    = 0L
    var adsRemoved   : Boolean = false
    var tutorialStep : Int     = 0

    // ── Goals ─────────────────────────────────────────────────────────────────

    var goalState: GoalState = GoalState()

    // ── Load signal ───────────────────────────────────────────────────────────
    // Стає true ПІСЛЯ повного loadFrom. Моделі, що залежать від збереженого
    // стану (напр. GoalsModel), чекають саме його — це усуває race з gridFlow.
    val isLoadedFlow = MutableStateFlow(false)

    // ── Persistence ───────────────────────────────────────────────────────────

    fun loadFrom(data: PlayerData) {
        // goalState ПЕРШИМ — щоб залежні моделі бачили його одразу
        goalState                 = data.goalState

        coinsFlow.value           = data.coins
        xpFlow.value              = data.xp
        mergeBonusCountFlow.value = data.mergeBonusCount
        mergeBonusGoalFlow.value  = data.mergeBonusGoal
        lastLoginTime             = data.lastLoginTime
        adsRemoved                = data.adsRemoved
        tutorialStep              = data.tutorialStep

        // grid — ОСТАННІМ серед flow, бо саме його чекають інші моделі
        gridFlow.value            = data.grid

        // сигнал "усе завантажено" — після всіх присвоєнь
        isLoadedFlow.value = true
    }

    fun toPlayerData() = PlayerData(
        coins           = coinsFlow.value,
        xp              = xpFlow.value,
        grid            = gridFlow.value,
        lastLoginTime   = lastLoginTime,
        adsRemoved      = adsRemoved,
        tutorialStep    = tutorialStep,
        mergeBonusCount = mergeBonusCountFlow.value,
        mergeBonusGoal  = mergeBonusGoalFlow.value,
        goalState       = goalState,
    )
}