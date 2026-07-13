package com.lewydo.idlemergecubes.game.dataStore

import com.lewydo.idlemergecubes.game.manager.AbstractDataStore
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import com.lewydo.idlemergecubes.game.data.AppJson

abstract class DataStoreJsonUtil<T>(
    protected val serializer  : KSerializer<T>,
    protected val deserializer: DeserializationStrategy<T>
) {
    val simpleName: String get() = this::class.java.simpleName

    abstract val coroutine: CoroutineScope
    abstract val flow     : MutableStateFlow<T>
    abstract val dataStore: AbstractDataStore.DataStoreElement<String>

    // Mutex гарантує що update-и виконуються строго один за одним,
    // навіть якщо їх викликають одночасно з кількох корутинів
    protected val mutex = Mutex()

    open fun initialize() {

        coroutine.launch(Dispatchers.IO) {

            val raw = dataStore.get()

            if (raw != null) {
                // Безпечне декодування: несумісний/пошкоджений save не крашить —
                // fallback на поточний flow.value (дефолт стора).
                val decoded = try {
                    AppJson.decodeFromString(deserializer, raw)
                } catch (e: Exception) {
                    log("[$simpleName] decode failed → default. ${e.message}")
                    flow.value
                }
                flow.value = decoded
                logInit(decoded)
            } else {
                log("[$simpleName] INIT → No saved data, using default")
                logInit(flow.value)
            }
        }
    }

    open fun update(block: (T) -> T) {

        coroutine.launch(Dispatchers.IO) {
            mutex.withLock {
                val oldValue = flow.value
                val newValue = block(oldValue)

                flow.value = newValue
                dataStore.update { AppJson.encodeToString(serializer, newValue) }

                logUpdate(oldValue, newValue)
            }
        }
    }

    private fun logInit(data: T) {
        log("""
        
        ╔══════════════════════════════╗
        ║  STORE INIT → $simpleName
        ╚══════════════════════════════╝
        $data
    """.trimIndent())
    }

    private fun logUpdate(old: T, new: T) {
        log("""
        
        ╔══════════════════════════════╗
        ║  STORE UPDATE → $simpleName
        ╠══════════════════════════════╣
        ║  OLD: $old
        ║  NEW: $new
        ╚══════════════════════════════╝
    """.trimIndent())
    }
}