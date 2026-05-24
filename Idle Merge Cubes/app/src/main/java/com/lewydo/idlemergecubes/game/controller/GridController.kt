package com.lewydo.idlemergecubes.game.controller

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACellLayer
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACube
import com.lewydo.idlemergecubes.game.actors.panelGrid.ACubeLayer
import com.lewydo.idlemergecubes.game.model.GridModel
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.services.tiktok.TikTokManager
import kotlinx.coroutines.CoroutineScope

class GridController(
    private val coroutine  : CoroutineScope?,
    private val gridModel  : GridModel,
    private val playerModel: PlayerModel,
    private val cellLayer  : ACellLayer,
    private val cubeLayer  : ACubeLayer,
) {

    // ------------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------------

    private sealed class Command {
        data class Move (val from: Int, val to: Int) : Command()
        data class Merge(val from: Int, val to: Int) : Command()
        data object Buy : Command()
    }

    // ------------------------------------------------------------------------
    // Queue
    // ------------------------------------------------------------------------

    private val queue = ArrayDeque<Command>()
    private var busy  = false

    private fun enqueue(cmd: Command) {
        queue.addLast(cmd)
        if (!busy) processNext()
    }

    private fun processNext() {
        val cmd = queue.removeFirstOrNull() ?: run { busy = false; return }
        busy = true
        when (cmd) {
            is Command.Move  -> executeMove(cmd.from, cmd.to)
            is Command.Merge -> executeMerge(cmd.from, cmd.to)
            is Command.Buy   -> executeBuy()
        }
    }

    private fun done() = processNext()

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    var onMergeExecuted: ((isEnchanted: Boolean) -> Unit)? = null

    fun move(from: Int, to: Int)  = enqueue(Command.Move(from, to))
    fun merge(from: Int, to: Int) = enqueue(Command.Merge(from, to))
    fun buyCube()                 = enqueue(Command.Buy)

    fun attachCube(cube: ACube)  = dragDelegate.attach(cube)
    fun isEmpty(index: Int)      = gridModel.isEmpty(index)
    fun getLevel(index: Int)     = gridModel.getLevel(index)
    fun isInteractionLocked()    = busy

    // ------------------------------------------------------------------------
    // Drag delegate
    // ------------------------------------------------------------------------

    private val dragDelegate = GridDragDelegate(
        controller = this,
        cellLayer  = cellLayer,
        cubeLayer  = cubeLayer,
    )

    // ------------------------------------------------------------------------
    // Initialize
    // ------------------------------------------------------------------------

    fun initialize() {
        gridModel.gridFlow.value.forEachIndexed { index, level ->
            if (level <= 0) return@forEachIndexed
            val cell   = cellLayer.getCell(index) ?: return@forEachIndexed
            val bounds = Rectangle(cell.x, cell.y, cell.width, cell.height)
            attachCube(cubeLayer.spawnCube(index, level, bounds))
        }
    }

    fun resetGrid() {
        queue.clear()
        busy = false
        gridModel.clearGrid()
        cubeLayer.clearAll()
    }

    // ------------------------------------------------------------------------
    // Execute — Move
    // ------------------------------------------------------------------------

    private fun executeMove(from: Int, to: Int) {
        if (!gridModel.isEmpty(to) || gridModel.isEmpty(from)) { done(); return }

        val cell = cellLayer.getCell(to) ?: run { done(); return }

        gridModel.move(from, to)
        cubeLayer.moveCube(from, to, tmpVec.set(cell.x, cell.y)) { done() }
    }

    // ------------------------------------------------------------------------
    // Execute — Merge
    // ------------------------------------------------------------------------

    private fun executeMerge(from: Int, to: Int) {
        val fromLevel = gridModel.getLevel(from)
        val toLevel   = gridModel.getLevel(to)
        if (fromLevel == 0 || fromLevel != toLevel) { done(); return }

        val cell     = cellLayer.getCell(to) ?: run { done(); return }
        val newLevel = gridModel.tryMerge(from, to) ?: run { done(); return }

        val xp    = calculateMergeXp(newLevel)
        val coins = calculateMergeCoins(newLevel)

        playerModel.addXp(xp)
        playerModel.addCoins(coins)

        cubeLayer.mergeCubes(from, to, tmpVec.set(cell.x, cell.y), xp, coins) {
            onMergeExecuted?.invoke(false)
            gdxGame.analytics.merge(newLevel)

            // Перевіряємо підвищення рівня BUY
            val upgrade = gdxGame.modelBuyLevel.checkAndUpgrade()
            if (upgrade != null) {
                cubeLayer.upgradeCubes(upgrade.upgradedIndices, upgrade.newLevel)
                GlobalEvents.emit(GlobalEvents.EventType.BUY_LEVEL_UPGRADED)
            }

            done()
        }
    }

    // ------------------------------------------------------------------------
    // Execute — Buy
    // ------------------------------------------------------------------------

    private fun executeBuy() {
        val price = playerModel.currentBuyPrice
        if (playerModel.currentCoins < price) { done(); return }
        if (!gridModel.hasEmptyCell())         { done(); return }
        if (!playerModel.spendCoins(price))    { done(); return }

        val level  = gdxGame.modelBuyLevel.currentBuyLevel
        val index  = gridModel.addCube(level) ?: run { done(); return }
        val cell   = cellLayer.getCell(index)  ?: run { done(); return }
        val bounds = Rectangle(cell.x, cell.y, cell.width, cell.height)

        attachCube(cubeLayer.spawnCube(index, level, bounds))

        gdxGame.analytics.buyCube(price)
        TikTokManager.spendCredits()
        done()
    }

    // ------------------------------------------------------------------------
    // Economy
    // ------------------------------------------------------------------------

    private fun calculateMergeCoins(cubeLevel: Int): Long =
        (cubeLevel * (1f + playerModel.currentLevel * 0.05f)).toLong()

    private fun calculateMergeXp(cubeLevel: Int): Long =
        (cubeLevel + (1f + playerModel.currentLevel * 0.05f)).toLong()

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------

    private val tmpVec = Vector2()
}