package com.lewydo.idlemergecubes.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val xp           : Long = 0L,
    val coins        : Long = 5_000L,
    val grid         : List<Int> = List(16) { 0 },
    val lastLoginTime: Long = 0L,
    val adsRemoved   : Boolean = false,
    val tutorialStep : Int     = 0,  // 0=BUY, 1=MERGE, 2=DONE
)