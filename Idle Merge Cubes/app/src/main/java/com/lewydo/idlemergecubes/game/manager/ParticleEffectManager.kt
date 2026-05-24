package com.lewydo.idlemergecubes.game.manager

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.ParticleEffect

class ParticleEffectManager(var assetManager: AssetManager) {

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    private val particleLoader = ParticleEffectLoader(InternalFileHandleResolver())

    var loadableParticleEffectList = mutableListOf<ParticleEffectData>()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    fun load() {
        assetManager.setLoader(ParticleEffect::class.java, ".p", particleLoader)
        loadableParticleEffectList.onEach { data ->
            val params = ParticleEffectLoader.ParticleEffectParameter().apply {
                imagesDir = MultiDirFileHandle(data.dirPaths)
            }
            assetManager.load(data.path, ParticleEffect::class.java, params)
        }
    }

    fun init() {
        loadableParticleEffectList.onEach { it.effect = assetManager[it.path, ParticleEffect::class.java] }
        loadableParticleEffectList.clear()
    }

    // ------------------------------------------------------------------------
    // Effects
    // ------------------------------------------------------------------------

    enum class EnumParticleEffect(val data: ParticleEffectData) {
        LOADER(ParticleEffectData("particle/loader/loader.p", "particle/loader", "particle/SHARED")),

        CONFETTI(ParticleEffectData("particle/confetti/confetti.p","particle/SHARED")),

        BUY         (ParticleEffectData("particle/buy/buy.p",  "particle/buy")),
        STAR        (ParticleEffectData("particle/buy/star.p", "particle/buy")),
        WAVE_UPGRADE(ParticleEffectData("particle/buy/wave_upgrade.p", "particle/buy")),

        IDLE_CONFETTI(ParticleEffectData("particle/idleIncome/confetti.p", "particle/SHARED")),
        IDLE_WAVE    (ParticleEffectData("particle/idleIncome/wave.p", "particle/idleIncome")),

        CUBE(ParticleEffectData("particle/cube/cube.p", "particle/cube", "particle/SHARED")),

        COLLECT(ParticleEffectData("particle/collect/collect.p", "particle/collect")),

    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    data class ParticleEffectData(
        val path    : String,
        val dirPaths: List<String>,
    ) {
        constructor(path: String, vararg dirs: String) : this(path, dirs.toList())

        lateinit var effect: ParticleEffect
    }

    class MultiDirFileHandle(
        private val dirs: List<String>
    ) : FileHandle() {
        override fun child(name: String): FileHandle {
            for (dir in dirs) {
                val handle = Gdx.files.internal("$dir/$name")
                if (handle.exists()) return handle
            }
            return Gdx.files.internal("${dirs.first()}/$name")
        }
    }

}