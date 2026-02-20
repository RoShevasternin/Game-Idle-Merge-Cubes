package com.lewydo.idlemergecubes.game.actors.panelGame

import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.util.currentClassName
import com.lewydo.idlemergecubes.util.log

class ACellLayer(
    override val screen: AdvancedScreen,
    private val rows: Int = 4,
    private val cols: Int = 4
): AdvancedGroup() {

    private val cells = mutableListOf<ACell>()


    override fun addActorsOnGroup() {
        addCells()
    }

    // Actors ------------------------------------------------------------------------

    private fun addCells() {
        val startX = 150f
        val startY = 1365f

        val cellSize = 392f
        val spacing  = 15f

        var index = 0

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val cell = ACell(screen, index)

                val x = startX + col * (cellSize + spacing)
                val y = startY - row * (cellSize + spacing)

                cell.setBounds(x, y, cellSize, cellSize)

                addActor(cell)
                cells.add(cell)

                index++
            }
        }
    }

    // Logic ------------------------------------------------------------------------

    fun getCell(index: Int): ACell? = cells.getOrNull(index)

    fun getAllCells(): List<ACell> = cells

    fun findCellIndexAt(stageX: Float, stageY: Float): Int? {

        val local = stageToLocalCoordinates(Vector2(stageX, stageY))
        log("$currentClassName = $local")

        return getAllCells().firstOrNull { cell ->
            local.x >= cell.x &&
            local.x <= cell.x + cell.width &&
            local.y >= cell.y &&
            local.y <= cell.y + cell.height
        }?.index
    }

}