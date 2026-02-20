package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Rectangle
import com.lewydo.idlemergecubes.game.actors.panelGame.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGame.ACube
import com.lewydo.idlemergecubes.game.actors.panelGame.ACubeLayer
import com.lewydo.idlemergecubes.game.model.GridModel
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class GridController(
    private val coroutine: CoroutineScope?,
    private val gridModel: GridModel,
    private val cellLayer: ACellLayer,
    private val cubeLayer: ACubeLayer
) {

    private val dragDelegate = GridDragDelegate(
        controller = this,
        cellLayer = cellLayer
    )

    init {
        observeGrid()
    }

    // ----------------------------
    // PUBLIC
    // ----------------------------

    private fun attachCube(cube: ACube) {
        dragDelegate.attach(cube)
    }

    fun spawn(level: Int) {
        val index = gridModel.addCube(level) ?: return
        // gridFlow сам створить куб через CubeLayer
    }

    fun move(from: Int, to: Int) {
        // викликається коли куб перемістили на пусту клітинку
        gridModel.move(from, to)
    }

    fun merge(from: Int, to: Int) {
        val newLevel = gridModel.tryMerge(from, to) ?: return
    }

    fun isEmpty(index: Int) = gridModel.isEmpty(index)

    fun getLevel(index: Int) = gridModel.getLevel(index)

    // ----------------------------
    // PRIVATE
    // ----------------------------

    private fun observeGrid() {
        coroutine?.launch {
            gridModel.gridFlow.collect { grid ->
                syncWithGrid(grid)
            }
        }
    }

    private fun syncWithGrid(grid: List<Int>) {
        for (i in grid.indices) {

            val level = grid[i]
            val cube  = cubeLayer.getCube(i)

            when {
                level == 0 && cube != null -> {
                    cubeLayer.removeCube(i)
                }
                level > 0 && cube == null -> {
                    val cell   = cellLayer.getCell(i) ?: return
                    val bounds = Rectangle(cell.x, cell.y, cell.width, cell.height)
                    val spawnedCube = cubeLayer.spawnCube(i, level, bounds)

                    attachCube(spawnedCube)
                }
                level > 0 && cube != null && cube.lvl != level -> {
                    cube.setLevel(level)
                }
            }
        }
    }
}