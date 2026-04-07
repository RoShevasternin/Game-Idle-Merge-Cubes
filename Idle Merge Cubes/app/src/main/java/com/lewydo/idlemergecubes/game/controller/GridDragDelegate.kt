package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACell
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACube
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACubeLayer

class GridDragDelegate(
    private val controller: GridController,
    private val cellLayer : ACellLayer,
    private val cubeLayer : ACubeLayer,
) {

    private var draggingCube    : ACube? = null
    private var lastCubeTouched : ACube? = null  // ← для rollback коли locked
    private var targetIndex     : Int?   = null
    private var lastTargetIndex : Int?   = null
    private var fromIndex       = -1
    private var dragAccepted    = false  // ← чи прийнятий поточний drag

    fun attach(cube: ACube) {
        cube.setDragCallbacks(
            onStart = { onStart(cube)        },
            onMove  = { x, y -> onMove(x, y) },
            onEnd   = { onEnd()              }
        )
    }

    private fun onStart(cube: ACube) {
        lastCubeTouched = cube

        if (controller.isInteractionLocked()) {
            dragAccepted        = false
            cube.isDragEnabled  = false  // ← блокуємо рух куба
            return
        }

        dragAccepted    = true
        draggingCube    = cube
        fromIndex       = cube.index

        cubeLayer.liftCube(fromIndex)
        updateCellsState()
    }

    private fun onMove(stageX: Float, stageY: Float) {
        if (!dragAccepted) return  // ← ігноруємо якщо не прийнято

        val cube = draggingCube ?: return
        val hoveredIndex = cellLayer.findCellIndexAt(stageX, stageY)
        val newTarget    = if (hoveredIndex == fromIndex) null else hoveredIndex

        if (newTarget == targetIndex) return

        targetIndex = newTarget
        updateCellsState()
        updateCubesStates()
    }

    private fun onEnd() {
        // Якщо drag не був прийнятий — просто rollback і чистимо
        if (!dragAccepted) {
            rollbackCube(lastCubeTouched)
            resetState()
            return
        }

        val cube   = draggingCube ?: run { resetState(); return }
        val target = targetIndex

        when {
            target == null                          -> rollback()
            controller.isEmpty(target)              -> controller.move(fromIndex, target)
            controller.getLevel(target) == cube.lvl -> controller.merge(fromIndex, target)
            else                                    -> rollback()
        }

        resetVisuals()
        resetState()
    }

    // ── Rollback helpers ─────────────────────────────────────────────────────

    private fun rollback() {
        val startCell = cellLayer.getCell(fromIndex) ?: return
        cubeLayer.moveCubeToPosition(fromIndex, Vector2(startCell.x, startCell.y))
    }

    private fun rollbackCube(cube: ACube?) {
        cube ?: return
        val startCell = cellLayer.getCell(cube.index) ?: return
        cubeLayer.moveCubeToPosition(cube.index, Vector2(startCell.x, startCell.y))
    }

    // ── State reset ───────────────────────────────────────────────────────────

    private fun resetState() {
        draggingCube    = null
        lastCubeTouched = null
        targetIndex     = null
        lastTargetIndex = null
        fromIndex       = -1
        dragAccepted    = false
    }

    // ── Cells / Cubes states ──────────────────────────────────────────────────

    private fun updateCubesStates() {
        val dragged = draggingCube ?: return
        val target  = targetIndex

        cubeLayer.getAllCubes().forEach { cube ->
            if (cube == dragged) return@forEach
            cube.setState(ACube.State.DEFAULT)
        }

        if (target == null) return
        val targetCube = cubeLayer.getCube(target) ?: return
        if (targetCube == dragged) return

        val levelAtTarget = controller.getLevel(target)
        when {
            levelAtTarget == dragged.lvl -> targetCube.setState(ACube.State.HOVER_MATCH)
            levelAtTarget != 0           -> targetCube.setState(ACube.State.HOVER_INVALID)
        }
    }

    private fun updateCellsState() {
        val dragged   = draggingCube ?: return
        val newTarget = targetIndex

        if (newTarget == lastTargetIndex) return

        // Скидаємо старий target
        lastTargetIndex?.let { oldIdx ->
            if (oldIdx != fromIndex) cellLayer.getCell(oldIdx)?.setState(ACell.State.DEFAULT)
        }

        // START клітинка
        cellLayer.getCell(fromIndex)?.setState(ACell.State.START, dragged.getVisualColor())

        // Новий target
        newTarget?.let { idx ->
            val level = controller.getLevel(idx)
            when (level) {
                0           -> cellLayer.getCell(idx)?.setState(ACell.State.HOVER_EMPTY)
                dragged.lvl -> cellLayer.getCell(idx)?.setState(ACell.State.HOVER_MATCH)
                else        -> cellLayer.getCell(idx)?.setState(ACell.State.HOVER_INVALID)
            }
        }

        lastTargetIndex = newTarget
    }

    private fun resetVisuals() {
        cubeLayer.getAllCubes().forEach { it.setState(ACube.State.DEFAULT) }
        cellLayer.getAllCells().forEach { it.setState(ACell.State.DEFAULT) }
    }
}