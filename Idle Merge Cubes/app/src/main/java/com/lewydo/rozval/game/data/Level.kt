package com.lewydo.rozval.game.data

import kotlinx.serialization.Serializable

@Serializable
data class Location(var isOpen: Boolean, val levels: List<Level>)

@Serializable
data class Level(var star: Int, var isOpen: Boolean)