package com.lewydo.idlemergecubes.game.dataStore

import com.lewydo.idlemergecubes.game.data.PlayerData
import com.lewydo.idlemergecubes.game.manager.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class DS_Player(override val coroutine: CoroutineScope): DataStoreJsonUtil<PlayerData>(
    serializer   = PlayerData.serializer(),
    deserializer = PlayerData.serializer(),
) {

    override val dataStore = DataStoreManager.Player

    override val flow = MutableStateFlow(PlayerData())

    init { initialize() }

}