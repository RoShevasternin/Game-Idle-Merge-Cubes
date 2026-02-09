package com.lewydo.rozval.game.dataStore

import com.lewydo.rozval.game.manager.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class DS_IsTutorial(override val coroutine: CoroutineScope): DataStoreUtil<Boolean>() {

    override val dataStore = DataStoreManager.IsTutorials

    override val flow = MutableStateFlow(false)

    init {
        initialize()
    }

}