package com.lewydo.rozval.game.dataStore

import com.lewydo.rozval.game.data.Level
import com.lewydo.rozval.game.data.Location
import com.lewydo.rozval.game.manager.DataStoreManager
import com.lewydo.rozval.game.utils.manager.LocationManager
import com.lewydo.rozval.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class DS_Location(override val coroutine: CoroutineScope): DataStoreJsonUtil<List<Location>>(
    serializer   = ListSerializer(Location.serializer()),
    deserializer = ListSerializer(Location.serializer()),
) {
    private val defaultLocationList = List<Location>(LocationManager.LocationType.entries.size) {
        Location(
            false,
            List<Level>(LocationManager.LocationType.entries[it].listLevelNecessaryStars.size) { Level(0, false) })
    }

    override val dataStore = DataStoreManager.Level

    override val flow = MutableStateFlow<List<Location>>(defaultLocationList)

    init {
        initialize()
    }

    override fun initialize() {
        coroutine.launch {
            DataStoreManager.Level.get()?.let { value -> flow.value = Json.decodeFromString<List<Location>>(value)}

            log("Store locations:")
            flow.value.onEachIndexed { index, location ->
                log("$index. ${LocationManager.LocationType.entries[index].name} = $location")
            }
        }
    }

    override fun update(block: (List<Location>) -> List<Location>) {
        coroutine.launch {
            flow.value = block(flow.value)

            log("Store locations update:")
            flow.value.onEachIndexed { index, location ->
                log("$index. ${LocationManager.LocationType.entries[index].name} = $location")
            }
            DataStoreManager.Level.update { Json.encodeToString<List<Location>>(flow.value) }
        }
    }

}