package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACube
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACubeLayer
import com.lewydo.idlemergecubes.game.model.GridModel
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GridController(
    private val coroutine  : CoroutineScope?,
    private val gridModel  : GridModel,
    private val playerModel: PlayerModel,
    private val cellLayer: ACellLayer,
    private val cubeLayer: ACubeLayer
) {

    // ======================================================
    // STATE
    // ======================================================
    private var isLocked = false

    // ======================================================
    // FIELD
    // ======================================================
    private val tmpVec = Vector2()

    // ======================================================
    // DRAG SYSTEM
    // ======================================================
    private val dragDelegate = GridDragDelegate(
        controller = this,
        cellLayer = cellLayer,
        cubeLayer = cubeLayer,
    )

    // ======================================================
    // INITIALIZATION
    // ======================================================
    fun initialize() {
        collectGrid()
    }

    // ======================================================
    // PUBLIC API
    // ======================================================

    fun attachCube(cube: ACube) {
        dragDelegate.attach(cube)
    }

    fun spawn(level: Int) {
        val index = gridModel.addCube(level) ?: return
        // gridFlow сам створить куб через CubeLayer
    }

    fun isEmpty(index: Int) = gridModel.isEmpty(index)

    fun getLevel(index: Int) = gridModel.getLevel(index)

    fun isInteractionLocked() = isLocked

    // ======================================================
    // MOVEMENT
    // ======================================================
    fun move(from: Int, to: Int) {
        if (isLocked) return

        val cell = cellLayer.getCell(to) ?: return
        val targetPos = tmpVec.set(cell.x, cell.y)

        cubeLayer.moveCube(from, to, targetPos) {
            gridModel.move(from, to)
        }
    }

    // ======================================================
    // MERGE
    // ======================================================
    fun merge(from: Int, to: Int) {
        if (isLocked) return

        val cell      = cellLayer.getCell(to) ?: return
        val targetPos = tmpVec.set(cell.x, cell.y)

        isLocked = true

        cubeLayer.mergeCubes(from, to, targetPos) {
            val newLevel = gridModel.tryMerge(from, to) ?: run {
                isLocked = false
                return@mergeCubes
            }

            playerModel.addXpFromMerge(newLevel)
            playerModel.addCoins(calculateMergeCoins(newLevel))

            isLocked = false
        }
    }

    // ======================================================
    // COLLECT
    // ======================================================

    private fun collectGrid() {
        coroutine?.launch {
            gridModel.gridFlow.collect { grid ->
                runGDX { syncWithGrid(grid) }
            }
        }
    }

    // ======================================================
    // GRID SYNC
    // ======================================================
    private fun syncWithGrid(grid: List<Int>) {
        for (i in grid.indices) {

            val level = grid[i]
            val cube  = cubeLayer.getCube(i)

            when {
                level == 0 && cube != null -> {
                    cubeLayer.removeCube(i)
                }
                level > 0 && cube == null -> {
                    val cell        = cellLayer.getCell(i) ?: continue
                    val bounds      = Rectangle(cell.x, cell.y, cell.width, cell.height)
                    val spawnedCube = cubeLayer.spawnCube(i, level, bounds)

                    attachCube(spawnedCube)
                }
                level > 0 && cube != null && cube.lvl != level -> {
                    cube.setLevel(level)
                }
            }
        }
    }

    // ======================================================
    // ECONOMY
    // ======================================================

    private fun calculateMergeCoins(cubeLevel: Int): Long {
        val level = playerModel.currentLevel
        val coins = cubeLevel * (1f + level * 0.05f)
        return coins.toLong()
    }

}