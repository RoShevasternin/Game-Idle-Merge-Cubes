package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.actors.panelGame.ACell
import com.lewydo.idlemergecubes.game.actors.panelGame.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGame.ACube
import com.lewydo.idlemergecubes.game.model.GridModel

class GridDragDelegate(
    private val controller: GridController,
    private val cellLayer: ACellLayer
) {

    private var draggingCube: ACube? = null
    private var targetIndex : Int? = null
    private var fromIndex = -1

    fun attach(cube: ACube) {
        cube.setDragCallbacks(
            onStart = { onStart(cube)        },
            onMove  = { x, y -> onMove(x, y) },
            onEnd   = { onEnd()              }
        )
    }

    private fun onStart(cube: ACube) {
        draggingCube = cube
        fromIndex    = cube.index

        cube.toFront()
        cube.animLift()

        cellLayer.getCell(fromIndex)?.setState(ACell.State.StartPos)
    }

    private fun onMove(stageX: Float, stageY: Float) {
        val cube      = draggingCube ?: return
        val newTarget = cellLayer.findCellIndexAt(stageX, stageY)

        if (newTarget == targetIndex) return

        resetCells() // окрім стартової клітинки
        targetIndex = newTarget ?: return

        val levelAtTarget = controller.getLevel(targetIndex!!)

        when (levelAtTarget) {
            0        -> cellLayer.getCell(targetIndex!!)?.setState(ACell.State.Target)
            cube.lvl -> cellLayer.getCell(targetIndex!!)?.setState(ACell.State.Green)
            else     -> cellLayer.getCell(targetIndex!!)?.setState(ACell.State.Red)
        }
    }

    private fun onEnd() {
        val cube   = draggingCube ?: return
        val target = targetIndex

        if (target == null) {
            rollback()
            return
        }

        when {
            controller.isEmpty(target)              -> controller.move(fromIndex, target)
            controller.getLevel(target) == cube.lvl -> controller.merge(fromIndex, target)
            else -> rollback()
        }

        resetCells()
        draggingCube = null
        targetIndex  = null
    }

    private fun rollback() {
        val startCell = cellLayer.getCell(fromIndex) ?: return
        draggingCube?.animMoveToCell(Vector2(startCell.x, startCell.y))
    }

    private fun resetCells() {
        cellLayer.getAllCells().forEach { it.reset() }
    }
}