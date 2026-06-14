package com.lewydo.idlemergecubes.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val xp             : Long      = 0L,
    val coins          : Long      = 10_000L,
    val grid           : List<Int> = List(16) { 0 },
    val lastLoginTime  : Long      = 0L,
    val adsRemoved     : Boolean   = false,
    val tutorialStep   : Int       = 0,
    val mergeBonusCount: Int       = 0,
    val mergeBonusGoal : Int       = 10,
    val goalState      : GoalState = GoalState(),  // ← один об'єкт замість 7 полів
)

@Serializable
data class GoalState(
    val typeName    : String                = "",
    val reward      : Long                  = 0L,
    val targetLevel : Int                   = 0,
    val timeLimitSec: Int                   = 0,
    val requirements: List<GoalRequirement> = emptyList(),
    val timerRemaining: Int                 = 0,
    val counter     : Int                   = 1,
)

@Serializable
data class GoalRequirement(val level: Int, val count: Int)