package com.lewydo.idlemergecubes.game.model

import com.lewydo.idlemergecubes.game.state.GameState

class GridModel(private val state: GameState) {

    // ------------------------------------------------------------------------
    // Flow
    // ------------------------------------------------------------------------

    val gridFlow = state.gridFlow

    // ------------------------------------------------------------------------
    // Read
    // ------------------------------------------------------------------------

    fun getLevel(index: Int) = state.gridFlow.value[index]
    fun isEmpty(index: Int)  = state.gridFlow.value[index] == 0
    fun hasEmptyCell()       = state.gridFlow.value.any { it == 0 }
    fun totalPower()         = state.gridFlow.value.sum()

    // ------------------------------------------------------------------------
    // Write
    // ------------------------------------------------------------------------

    fun addCube(level: Int): Int? {
        val grid  = state.gridFlow.value.toMutableList()
        val index = grid.indexOfFirst { it == 0 }
        if (index == -1) return null
        grid[index] = level
        state.gridFlow.value = grid
        return index
    }

    fun move(from: Int, to: Int): Boolean {
        val grid = state.gridFlow.value
        if (grid[from] == 0 || grid[to] != 0) return false
        val new   = grid.toMutableList()
        new[to]   = new[from]
        new[from] = 0
        state.gridFlow.value = new
        return true
    }

    fun tryMerge(from: Int, to: Int): Int? {
        val grid      = state.gridFlow.value
        val fromLevel = grid[from]
        if (fromLevel == 0 || fromLevel != grid[to]) return null
        val new      = grid.toMutableList()
        val newLevel = fromLevel + 1
        new[to]      = newLevel
        new[from]    = 0
        state.gridFlow.value = new
        return newLevel
    }

    fun clearGrid() {
        state.gridFlow.value = List(16) { 0 }
    }
}