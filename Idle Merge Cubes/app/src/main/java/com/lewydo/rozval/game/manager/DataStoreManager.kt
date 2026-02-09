package com.lewydo.rozval.game.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.lewydo.rozval.appContext
import kotlinx.coroutines.flow.first

object DataStoreManager: AbstractDataStore() {
    override val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DATA_STORE")

    object Star: DataStoreElement<Int>() {
        override val key = intPreferencesKey("star")
    }
    object Key: DataStoreElement<Int>() {
        override val key = intPreferencesKey("key")
    }
    object Level: DataStoreElement<String>() {
        override val key = stringPreferencesKey("level")
    }


    // Ще не використовуються
    object IsTutorials: DataStoreElement<Boolean>() {
        override val key = booleanPreferencesKey("isTutorials")
    }


}

abstract class AbstractDataStore {
    abstract val Context.dataStore: DataStore<Preferences>

    abstract inner class DataStoreElement<T> {
        abstract val key: Preferences.Key<T>

        open suspend fun collect(block: suspend (T?) -> Unit) {
            appContext.dataStore.data.collect { block(it[key]) }
        }

        open suspend fun update(block: suspend (T?) -> T) {
            appContext.dataStore.edit { it[key] = block(it[key]) }
        }

        open suspend fun get(): T? {
            return appContext.dataStore.data.first()[key]
        }
    }
}

