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

    // ── Persistence ───────────────────────────────────────────────────────────

    fun loadFrom(data: PlayerData) {
        coinsFlow.value           = data.coins
        xpFlow.value              = data.xp
        gridFlow.value            = data.grid
        mergeBonusCountFlow.value = data.mergeBonusCount
        mergeBonusGoalFlow.value  = data.mergeBonusGoal
        lastLoginTime             = data.lastLoginTime
        adsRemoved                = data.adsRemoved
        tutorialStep              = data.tutorialStep
        goalState                 = data.goalState
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