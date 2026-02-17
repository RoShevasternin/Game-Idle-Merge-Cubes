package com.lewydo.idlemergecubes.game.dataStore

import com.lewydo.idlemergecubes.game.manager.AbstractDataStore
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

abstract class DataStoreJsonUtil<T>(
    private val serializer  : KSerializer<T>,
    private val deserializer: DeserializationStrategy<T>
) {
    val simpleName: String get() = this::class.java.simpleName

    abstract val coroutine: CoroutineScope
    abstract val flow     : MutableStateFlow<T>
    abstract val dataStore: AbstractDataStore.DataStoreElement<String>

    open fun initialize() {
        coroutine.launch(Dispatchers.IO) {
            dataStore.get()?.let { value -> flow.update { Json.decodeFromString(deserializer, value) } }
            log("Store $simpleName = ${flow.value}")
        }
    }

    open fun update(block: (T) -> T) {
        coroutine.launch(Dispatchers.IO) {
            flow.update { block(flow.value) }

            log("Store $simpleName update = ${flow.value}")
            dataStore.update { Json.encodeToString(serializer, flow.value) }
        }
    }
}
