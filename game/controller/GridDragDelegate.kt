package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACell
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACube
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACubeLayer

class GridDragDelegate(
    private val controller: GridController,
    private val cellLayer: ACellLayer,
    private val cubeLayer: ACubeLayer,
) {

    // ------------------------------------------------------------------------
    // DRAG STATE
    // ------------------------------------------------------------------------

    private var draggingCube: ACube? = null
    private var targetIndex : Int? = null
    private var fromIndex = -1
    private var lastTargetIndex: Int? = null

    // ------------------------------------------------------------------------
    // ATTACH
    // ------------------------------------------------------------------------

    fun attach(cube: ACube) {
        cube.setDragCallbacks(
            onStart = { onStart(cube)        },
            onMove  = { x, y -> onMove(x, y) },
            onEnd   = { onEnd()              }
        )
    }

    // ------------------------------------------------------------------------
    // DRAG START
    // ------------------------------------------------------------------------

    private fun onStart(cube: ACube) {
        if (controller.isInteractionLocked()) return

        draggingCube = cube
        fromIndex    = cube.index

        cubeLayer.liftCube(fromIndex)

        updateCellsState()
    }

    // ------------------------------------------------------------------------
    // DRAG MOVE
    // ------------------------------------------------------------------------

    private fun onMove(stageX: Float, stageY: Float) {
        val cube = draggingCube ?: return
        val hoveredIndex = cellLayer.findCellIndexAt(stageX, stageY)

        val newTarget = if (hoveredIndex == fromIndex) null else hoveredIndex

        if (newTarget == targetIndex) return  // 🔥 ВАЖЛИВО

        targetIndex = newTarget

        updateCellsState()
        updateCubesStates()
    }

    // ------------------------------------------------------------------------
    // DRAG END
    // ------------------------------------------------------------------------

    private fun onEnd() {
        val cube   = draggingCube ?: return
        val target = targetIndex

        when {
            target == null                          -> rollback()
            controller.isEmpty(target)              -> controller.move(fromIndex, target)
            controller.getLevel(target) == cube.lvl -> controller.merge(fromIndex, target)
            else                                    -> rollback()
        }

        resetVisuals()

        draggingCube = null
        targetIndex = null
    }

    // ------------------------------------------------------------------------
    // CUBE SYSTEM
    // ------------------------------------------------------------------------

    private fun updateCubesStates() {

        val dragged = draggingCube
        val target  = targetIndex

        // 1️⃣ Скидаємо всі КРІМ dragging
        cubeLayer.getAllCubes().forEach { cube ->
            if (cube == dragged) return@forEach
            cube.setState(ACube.State.DEFAULT)
        }

        if (dragged == null) return
        if (target == null) return

        val levelAtTarget = controller.getLevel(target)
        val targetCube    = cubeLayer.getCube(target) ?: return

        // Не чіпаємо якщо це той самий куб
        if (targetCube == dragged) return

        when {
            levelAtTarget == dragged.lvl -> targetCube.setState(ACube.State.HOVER_MATCH)
            levelAtTarget != 0           -> targetCube.setState(ACube.State.HOVER_INVALID)
        }
    }

    // ------------------------------------------------------------------------
    // CELL SYSTEM
    // ------------------------------------------------------------------------

    private fun updateCellsState() {
        val dragged = draggingCube ?: return

        val newTarget = targetIndex

        if (newTarget == lastTargetIndex) return

        // 1️⃣ скинути старий target
        lastTargetIndex?.let { oldIndex ->
            if (oldIndex != fromIndex) {
                cellLayer.getCell(oldIndex)?.setState(ACell.State.DEFAULT)
            }
        }

        // 2️⃣ стартова клітинка завжди START
        cellLayer.getCell(fromIndex)
            ?.setState(ACell.State.START, dragged.getVisualColor())

        // 3️⃣ якщо є новий target — встановити його
        newTarget?.let { index ->
            val levelAtTarget = controller.getLevel(index)
            val targetCell    = cellLayer.getCell(index)

            when (levelAtTarget) {
                0           -> targetCell?.setState(ACell.State.HOVER_EMPTY)
                dragged.lvl -> targetCell?.setState(ACell.State.HOVER_MATCH)
                else        -> targetCell?.setState(ACell.State.HOVER_INVALID)
            }
        }

        lastTargetIndex = newTarget
    }

    // ------------------------------------------------------------------------
    // RESET
    // ------------------------------------------------------------------------

    private fun resetVisuals() {
        cubeLayer.getAllCubes().forEach { it.setState(ACube.State.DEFAULT) }
        cellLayer.getAllCells().forEach { it.setState(ACell.State.DEFAULT) }
    }


    // ------------------------------------------------------------------------
    // ROLLBACK
    // ------------------------------------------------------------------------

    private fun rollback() {
        val startCell = cellLayer.getCell(fromIndex) ?: return
        cubeLayer.moveCubeToPosition(fromIndex, Vector2(startCell.x, startCell.y))
    }

}