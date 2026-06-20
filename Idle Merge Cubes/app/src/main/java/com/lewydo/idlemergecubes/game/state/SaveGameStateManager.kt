package com.lewydo.idlemergecubes.game.state

import com.lewydo.idlemergecubes.game.data.PlayerData
import com.lewydo.idlemergecubes.game.manager.DataStoreManager
import com.lewydo.idlemergecubes.game.model.PlayerModel
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

class SaveGameStateManager(
    private val gameState  : GameState,
    private val scope      : CoroutineScope
) {

    private val dataStore  = DataStoreManager.Player
    private val mutex      = Mutex()
    private var autoSaveJob: Job? = null

    // ------------------------------------------------------------------------
    // Load — при старті гри
    // ------------------------------------------------------------------------

    fun load() {
        scope.launch(Dispatchers.IO) {
            val raw  = dataStore.get()
            val data = if (raw != null) {
                Json.decodeFromString(PlayerData.serializer(), raw)
            } else {
                PlayerData() // ← дефолтні значення з PlayerData
            }
            gameState.loadFrom(data)
            logState("GAME STATE LOADED", data)
        }
    }

    // ------------------------------------------------------------------------
    // Save — при паузі або вручну
    // ------------------------------------------------------------------------

    fun save() {
        scope.launch(Dispatchers.IO) {
            mutex.withLock {
                val data = gameState.toPlayerData()
                val json = Json.encodeToString(PlayerData.serializer(), data)
                dataStore.update { json }
                logState("GAME STATE SAVED", data)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Auto save — запускати в onCreate, зупиняти в onDestroy
    // ------------------------------------------------------------------------

    fun startAutoSave(intervalSec: Int = 30) {
        autoSaveJob?.cancel()
        autoSaveJob = scope.launch(Dispatchers.IO) {
            while (true) {
                delay((intervalSec * 1000L).milliseconds)
                mutex.withLock {
                    val data = gameState.toPlayerData()
                    val json = Json.encodeToString(PlayerData.serializer(), data)
                    dataStore.update { json }
                    logState("GAME STATE AUTO-SAVED", data)
                }
            }
        }
    }

    fun stopAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = null
    }

    // ------------------------------------------------------------------------
    // Log
    // ------------------------------------------------------------------------

    private fun logState(title: String, data: PlayerData) {
        val gs = data.goalState

        // тип задачі + її суть у читабельному вигляді
        val goalDesc = when {
            gs.typeName.isBlank()        -> "—"
            gs.typeName == "ReachLevel"  -> "ReachLevel(target=${gs.targetLevel})"
            gs.typeName == "Collect"     -> "Collect(${gs.requirements.joinToString { "lvl${it.level}x${it.count}" }})"
            else                         -> gs.typeName
        }
        val timed = if (gs.timeLimitSec > 0) "${gs.timerRemaining}/${gs.timeLimitSec}s" else "—"

        // grid у компактному вигляді: непорожні клітинки
        val gridCells = data.grid.count { it > 0 }
        val gridMax   = data.grid.maxOrNull() ?: 0

        log("""
        
        ╔════════════════════════════════════════════╗
        ║  $title
        ╠════════════════════════════════════════════╣
        ║  Coins        : ${data.coins}
        ║  XP           : ${data.xp}
        ║  AdsRemoved   : ${data.adsRemoved}
        ║  TutorialStep : ${data.tutorialStep}
        ║  MergeBonus   : ${data.mergeBonusCount} / ${data.mergeBonusGoal}
        ║  Grid         : $gridCells cells, max lvl $gridMax
        ╠═══════════════ GOALS ═══════════════════════╣
        ║  Goal #       : ${gs.counter}
        ║  Type         : ${gs.typeName.ifBlank { "—" }}
        ║  Objective    : $goalDesc
        ║  Reward       : ${gs.reward}
        ║  Timer        : $timed
        ╚════════════════════════════════════════════╝
    """.trimIndent())
    }
}