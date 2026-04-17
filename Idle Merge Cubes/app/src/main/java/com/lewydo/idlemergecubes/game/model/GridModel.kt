package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.dataStore.DS_Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

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

    private val localGrid: MutableList<Int> = ds.flow.value.grid.toMutableList()

    fun syncLocalGrid(grid: List<Int>) {
        localGrid.clear()
        localGrid.addAll(grid)
    }

    fun getLevel(index: Int) = localGrid[index]

    fun isEmpty(index: Int) = localGrid[index] == 0

    fun hasEmptyCell() = localGrid.any { it == 0 }

    fun totalPower() = localGrid.sum()

    fun addCube(level: Int): Int? {
        val emptyIndex = localGrid.indexOfFirst { it == 0 }
        if (emptyIndex == -1) return null

        localGrid[emptyIndex] = level          // ← синхронно
        ds.update { it.copy(grid = localGrid.toList()) }  // ← persist async

        return emptyIndex
    }

    fun move(from: Int, to: Int): Boolean {
        if (localGrid[from] == 0) return false
        if (localGrid[to]   != 0) return false

        localGrid[to]   = localGrid[from]
        localGrid[from] = 0
        ds.update { it.copy(grid = localGrid.toList()) }

        return true
    }

    fun tryMerge(from: Int, to: Int): Int? {
        val fromLevel = localGrid[from]
        val toLevel   = localGrid[to]

        if (fromLevel == 0 || fromLevel != toLevel) return null

        val newLevel = fromLevel + 1
        localGrid[to]   = newLevel
        localGrid[from] = 0
        ds.update { it.copy(grid = localGrid.toList()) }

        return newLevel
    }

    fun clearGrid() {
        localGrid.fill(0)
        ds.update { it.copy(grid = List(16) { 0 }) }
    }
}
