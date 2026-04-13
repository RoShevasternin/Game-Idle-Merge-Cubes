package com.lewydo.idlemergecubes.game.utils

object GlobalEvents {

    enum class EventType {
        END_FLY_COIN, END_FLY_XP
    }

    private val eventsMap = Array(EventType.entries.size) { mutableListOf<Block>() }

    fun registerEvent(type: EventType, event: Block) {
        eventsMap[type.ordinal].add(event)
    }

    fun invokeEvent(type: EventType) {
        eventsMap[type.ordinal].forEach { it.invoke() }
    }
}