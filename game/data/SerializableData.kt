package com.lewydo.idlemergecubes.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val xp           : Long = 0L,
    val coins        : Long = 0L,
    val grid         : List<Int> = List(16) { 0 },
    val lastLoginTime: Long = 0L,
    val adsRemoved   : Boolean = false
)