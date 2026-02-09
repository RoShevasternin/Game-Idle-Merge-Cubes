package com.lewydo.rozval.game.dataStore

import com.lewydo.rozval.game.manager.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class DS_Key(override val coroutine: CoroutineScope): DataStoreUtil<Int>() {

    override val dataStore = DataStoreManager.Key

    override val flow = MutableStateFlow(0)

    init {
        initialize()
    }

}