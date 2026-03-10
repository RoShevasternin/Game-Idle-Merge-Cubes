package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.dataStore.DS_Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GridModel(
    private val ds: DS_Player,
    scope: CoroutineScope
) {

    val gridFlow: StateFlow<List<Int>> =
        ds.flow.map { it.grid }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = ds.flow.value.grid
            )

    private val currentGrid: List<Int>
        get() = gridFlow.value

    fun getLevel(index: Int): Int {
        return currentGrid[index]
    }

    fun isEmpty(index: Int): Boolean {
        return currentGrid[index] == 0
    }

    fun addCube(level: Int): Int? {

        val emptyIndex = currentGrid.indexOfFirst { it == 0 }
        if (emptyIndex == -1) return null

        ds.update { data ->
            val newGrid = data.grid.toMutableList()
            newGrid[emptyIndex] = level
            data.copy(grid = newGrid)
        }

        return emptyIndex
    }

    fun move(from: Int, to: Int): Boolean {

        if (currentGrid[from] == 0) return false
        if (currentGrid[to] != 0) return false

        ds.update { data ->
            val newGrid = data.grid.toMutableList()
            newGrid[to] = newGrid[from]
            newGrid[from] = 0
            data.copy(grid = newGrid)
        }

        return true
    }

    fun tryMerge(from: Int, to: Int): Int? {

        val fromLevel = currentGrid[from]
        val toLevel   = currentGrid[to]

        if (fromLevel == 0) return null
        if (fromLevel != toLevel) return null

        val newLevel = fromLevel + 1

        ds.update { data ->
            val newGrid = data.grid.toMutableList()
            newGrid[to] = newLevel
            newGrid[from] = 0
            data.copy(grid = newGrid)
        }

        return newLevel
    }

    fun totalPower(): Int {
        return currentGrid.sum()
    }

    fun hasEmptyCell(): Boolean {
        return currentGrid.any { it == 0 }
    }
}
