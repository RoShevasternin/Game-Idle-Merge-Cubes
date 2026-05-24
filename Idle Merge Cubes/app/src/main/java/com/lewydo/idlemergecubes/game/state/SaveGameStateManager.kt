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
            logLoad(data)
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
                logSave(data)
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
                delay(intervalSec * 1000L)
                mutex.withLock {
                    val data = gameState.toPlayerData()
                    val json = Json.encodeToString(PlayerData.serializer(), data)
                    dataStore.update { json }
                    log("SaveGameStateManager: auto-save ✓")
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

    private fun logLoad(data: PlayerData) {
        log("""
        
        ╔══════════════════════════════╗
        ║  GAME STATE LOADED
        ╠══════════════════════════════╣
        ║  Coins:        ${data.coins}
        ║  XP:           ${data.xp}
        ║  AdsRemoved:   ${data.adsRemoved}
        ║  TutorialStep: ${data.tutorialStep}
        ║  MergeBonus:   ${data.mergeBonusCount} / ${data.mergeBonusGoal}
        ╚══════════════════════════════╝
    """.trimIndent())
    }

    private fun logSave(data: PlayerData) {
        log("""
        
        ╔══════════════════════════════╗
        ║  GAME STATE SAVED
        ╠══════════════════════════════╣
        ║  Coins:        ${data.coins}
        ║  XP:           ${data.xp}
        ║  AdsRemoved:   ${data.adsRemoved}
        ║  TutorialStep: ${data.tutorialStep}
        ║  MergeBonus:   ${data.mergeBonusCount} / ${data.mergeBonusGoal}
        ╚══════════════════════════════╝
    """.trimIndent())
    }
}