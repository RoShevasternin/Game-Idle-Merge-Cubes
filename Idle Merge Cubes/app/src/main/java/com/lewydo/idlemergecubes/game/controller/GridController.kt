package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACube
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACubeLayer
import com.lewydo.idlemergecubes.game.model.GridModel
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.tiktok.TikTokManager
import kotlinx.coroutines.CoroutineScope

class GridController(
    private val coroutine  : CoroutineScope?,
    private val gridModel  : GridModel,
    private val playerModel: PlayerModel,
    private val cellLayer: ACellLayer,
    private val cubeLayer: ACubeLayer
) {

    // ======================================================
    // COMMANDS
    // ======================================================

    private sealed class Command {
        data class Move (val from: Int, val to: Int) : Command()
        data class Merge(val from: Int, val to: Int) : Command()
        object Buy : Command()
    }

    // ======================================================
    // QUEUE STATE
    // ======================================================

    private val queue = ArrayDeque<Command>()
    private var busy  = false

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
        val savedGrid = gridModel.gridFlow.value
        gridModel.syncLocalGrid(savedGrid)
        initGrid()
    }

    // ======================================================
    // PUBLIC API
    // ======================================================

    fun attachCube(cube: ACube) {
        dragDelegate.attach(cube)
    }

    fun isEmpty(index: Int)  = gridModel.isEmpty(index)
    fun getLevel(index: Int) = gridModel.getLevel(index)

    // Drag делегат питає — чи можна починати drag прямо зараз
    fun isInteractionLocked() = busy

    // ======================================================
    // ENQUEUE
    // ======================================================

    fun move(from: Int, to: Int) {
        enqueue(Command.Move(from, to))
    }

    fun merge(from: Int, to: Int) {
        enqueue(Command.Merge(from, to))
    }

    fun buyCube() {
        enqueue(Command.Buy)
    }

    // ======================================================
    // QUEUE ENGINE
    // ======================================================

    private fun enqueue(cmd: Command) {
        queue.addLast(cmd)
        if (!busy) processNext()
    }

    private fun processNext() {
        val cmd = queue.removeFirstOrNull() ?: run {
            busy = false
            return
        }

        busy = true

        when (cmd) {
            is Command.Move  -> executeMove(cmd.from, cmd.to)
            is Command.Merge -> executeMerge(cmd.from, cmd.to)
            is Command.Buy   -> executeBuy()
        }
    }

    private fun done() {
        processNext()
    }

    // ======================================================
    // EXECUTE — MOVE
    // ======================================================

    private fun executeMove(from: Int, to: Int) {

        // Перевіряємо актуальний стан моделі
        if (!gridModel.isEmpty(to) || gridModel.isEmpty(from)) {
            done(); return
        }

        val cell      = cellLayer.getCell(to) ?: run { done(); return }
        val targetPos = tmpVec.set(cell.x, cell.y)

        // Оновлюємо модель одразу — до анімації
        // Так наступна команда в черзі вже бачить правильний стан
        gridModel.move(from, to)

        cubeLayer.moveCube(from, to, targetPos) {
            done()
        }
    }

    // ======================================================
    // EXECUTE — MERGE
    // ======================================================

    private fun executeMerge(from: Int, to: Int) {
        // Перевіряємо актуальний стан моделі
        val fromLevel = gridModel.getLevel(from)
        val toLevel   = gridModel.getLevel(to)

        if (fromLevel == 0 || fromLevel != toLevel) {
            done(); return
        }

        val cell      = cellLayer.getCell(to) ?: run { done(); return }
        val targetPos = tmpVec.set(cell.x, cell.y)

        // Оновлюємо модель одразу — до анімації
        val newLevel = gridModel.tryMerge(from, to) ?: run { done(); return }

        val xp    = calculateMergeXp(newLevel)
        val coins = calculateMergeCoins(newLevel)

        playerModel.addXp(xp)
        playerModel.addCoins(coins)

        cubeLayer.mergeCubes(from, to, targetPos, xp, coins) {
            gdxGame.analytics.merge(newLevel)
            done()
        }
    }

    // ======================================================
    // EXECUTE — BUY
    // ======================================================

    private fun executeBuy() {

        val price = playerModel.currentBuyPrice

        if (playerModel.currentCoins < price) { done(); return }
        if (!gridModel.hasEmptyCell())        { done(); return }
        if (!playerModel.spendCoins(price))   { done(); return }

        val cubeLvL = 1

        val index = gridModel.addCube(cubeLvL) ?: run { done(); return }

        // Спавнимо куб вручну — як раніше робила syncWithGrid
        val cell   = cellLayer.getCell(index) ?: run { done(); return }
        val bounds = Rectangle(cell.x, cell.y, cell.width, cell.height)
        attachCube(cubeLayer.spawnCube(index, cubeLvL, bounds))

        gdxGame.analytics.buyCube(gdxGame.modelPlayer.buyPriceFlow.value)
        TikTokManager.spendCredits()
        done()
    }

    private fun initGrid() {
        val grid = gridModel.gridFlow.value  // читаємо поточний стан одразу
        for (i in grid.indices) {
            val level = grid[i]
            if (level > 0) {
                val cell   = cellLayer.getCell(i) ?: continue
                val bounds = Rectangle(cell.x, cell.y, cell.width, cell.height)
                attachCube(cubeLayer.spawnCube(i, level, bounds))
            }
        }
    }

    fun resetGrid() {
        // ❗ чистимо queue
        queue.clear()
        busy = false

        // ❗ чистимо модель
        gridModel.clearGrid()

        // ❗ чистимо візуал
        cubeLayer.clearAll()
    }

    // ======================================================
    // ECONOMY
    // ======================================================

    private fun calculateMergeCoins(cubeLevel: Int): Long {
        val level = playerModel.currentLevel
        val coins = cubeLevel * (1f + level * 0.05f)
        return coins.toLong()
    }

    private fun calculateMergeXp(cubeLevel: Int): Long {
        val level = playerModel.currentLevel
        val xp    = cubeLevel + (1f + level * 0.05f)
        return xp.toLong()
    }



}