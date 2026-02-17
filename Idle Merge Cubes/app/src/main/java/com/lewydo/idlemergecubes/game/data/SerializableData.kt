package com.lewydo.idlemergecubes.game.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val xp           : Long = 0L,
    val coins        : Long = 0L,
    val lastLoginTime: Long = 0L,
    val adsRemoved   : Boolean = false
)