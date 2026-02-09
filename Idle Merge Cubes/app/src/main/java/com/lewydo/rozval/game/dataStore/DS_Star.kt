package com.lewydo.rozval.game.dataStore

import com.lewydo.rozval.game.manager.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class DS_Star(override val coroutine: CoroutineScope): DataStoreUtil<Int>() {

    override val dataStore = DataStoreManager.Star

    override val flow = MutableStateFlow(0)

    init {
        initialize()
    }
}