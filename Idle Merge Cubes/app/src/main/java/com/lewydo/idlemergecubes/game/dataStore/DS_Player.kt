//package com.lewydo.idlemergecubes.game.dataStore
//
//import com.lewydo.idlemergecubes.game.data.PlayerData
//import com.lewydo.idlemergecubes.game.manager.DataStoreManager
//import com.lewydo.idlemergecubes.util.log
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import kotlinx.serialization.json.Json
//
//class DS_Player(override val coroutine: CoroutineScope): DataStoreJsonUtil<PlayerData>(
//    serializer   = PlayerData.serializer(),
//    deserializer = PlayerData.serializer(),
//) {
//
//    override val dataStore = DataStoreManager.Player
//
//    override val flow = MutableStateFlow(PlayerData())
//
//    init { initialize() }
//
//    override fun initialize() {
//        coroutine.launch(Dispatchers.IO) {
//
//            val raw = dataStore.get()
//
//            if (raw != null) {
//                val decoded = Json.decodeFromString(deserializer, raw)
//                flow.value = decoded
//                logPlayerInit(decoded)
//            } else {
//                log("DS_Player INIT → Default data")
//                logPlayerInit(flow.value)
//            }
//        }
//    }
//
//    override fun update(block: (PlayerData) -> PlayerData) {
//        coroutine.launch(Dispatchers.IO) {
//            mutex.withLock {
//                val old = flow.value
//                val new = block(old)
//
//                flow.value = new
//                dataStore.update { Json.encodeToString(serializer, new) }
//
//                logPlayerUpdate(old, new)
//            }
//        }
//    }
//
//    private fun logGridInBox(title: String, grid: List<Int>, size: Int = 4) {
//
//        val cellWidth = 5 // ширина однієї клітинки (важливо непарне число)
//
//        fun center(text: String, width: Int): String {
//            val padding = width - text.length
//            val padStart = padding / 2
//            val padEnd = padding - padStart
//            return " ".repeat(padStart) + text + " ".repeat(padEnd)
//        }
//
//        val rows = mutableListOf<String>()
//
//        for (row in 0 until size) {
//
//            val rowBuilder = StringBuilder()
//
//            for (col in 0 until size) {
//                val index = row * size + col
//                val value = grid[index]
//
//                val cell = if (value == 0) "-" else value.toString()
//                rowBuilder.append(center(cell, cellWidth))
//
//                if (col != size - 1) rowBuilder.append("│")
//            }
//
//            rows.add(rowBuilder.toString())
//        }
//
//        val contentWidth = rows.maxOf { it.length }
//
//        log("╔${"═".repeat(contentWidth)}╗")
//        log("║${center(title, contentWidth)}║")
//        log("╠${"═".repeat(contentWidth)}╣")
//
//        rows.forEachIndexed { index, row ->
//            log("║$row║")
//
//            if (index != size - 1) {
//                val separator = buildString {
//                    for (col in 0 until size) {
//                        append("─".repeat(cellWidth))
//                        if (col != size - 1) append("┼")
//                    }
//                }
//                log("║$separator║")
//            }
//        }
//
//        log("╚${"═".repeat(contentWidth)}╝")
//    }
//
//    private fun logPlayerInit(data: PlayerData) {
//        log("""
//
//    ╔══════════════════════════════╗
//    ║  PLAYER INIT
//    ╠══════════════════════════════╣
//    ║  XP:           ${data.xp}
//    ║  Coins:        ${data.coins}
//    ║  LastLogin:    ${data.lastLoginTime}
//    ║  AdsRemoved:   ${data.adsRemoved}
//    ║  TutorialStep: ${data.tutorialStep}
//    ╚══════════════════════════════╝
//""".trimIndent())
//
//        logGridInBox("GRID STATE", data.grid)
//    }
//
//    private fun logPlayerUpdate(old: PlayerData, new: PlayerData) {
//        val changes = buildList {
//            if (old.xp           != new.xp)            add("XP:           ${old.xp} → ${new.xp}")
//            if (old.coins        != new.coins)         add("Coins:        ${old.coins} → ${new.coins}")
//            if (old.lastLoginTime!= new.lastLoginTime) add("LastLogin:    ${old.lastLoginTime} → ${new.lastLoginTime}")
//            if (old.adsRemoved   != new.adsRemoved)    add("AdsRemoved:   ${old.adsRemoved} → ${new.adsRemoved}")
//            if (old.tutorialStep != new.tutorialStep)  add("TutorialStep: ${old.tutorialStep} → ${new.tutorialStep}")
//        }
//
//        if (changes.isNotEmpty()) {
//            val body = changes.joinToString("\n") { "║  $it" }
//            log("\n╔══════════════════════════════╗\n║  PLAYER UPDATE\n╠══════════════════════════════╣\n$body\n╚══════════════════════════════╝")
//        }
//
//        if (old.grid != new.grid) logGridInBox("GRID UPDATE", new.grid)
//    }
//
//}