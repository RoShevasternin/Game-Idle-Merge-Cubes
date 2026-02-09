package com.lewydo.rozval.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.ParticleEffect

class ParticleEffectManager(var assetManager: AssetManager) {

    private val params         = ParticleEffectLoader.ParticleEffectParameter()
    private val particleLoader = ParticleEffectLoader(InternalFileHandleResolver())

    var loadableParticleEffectList = mutableListOf<ParticleEffectData>()

    fun load() {
        assetManager.setLoader(ParticleEffect::class.java, ".p", particleLoader)
        loadableParticleEffectList.onEach { assetManager.load(it.path, ParticleEffect::class.java, params.apply {
            atlasFile = "particle/particle.atlas"
        }) }
    }

    fun init() {
        loadableParticleEffectList.onEach { it.effect = assetManager[it.path, ParticleEffect::class.java] }
        loadableParticleEffectList.clear()
    }

    enum class EnumParticleEffect(val data: ParticleEffectData) {
        Boom_1(ParticleEffectData("particle/boom 1.p")),
        Boom_2(ParticleEffectData("particle/boom 2.p")),
        Boom_3(ParticleEffectData("particle/boom 3.p")),
        Boom_4(ParticleEffectData("particle/boom 4.p")),
        Boom_5(ParticleEffectData("particle/boom 5.p")),
        Boom_6(ParticleEffectData("particle/boom 6.p")),
        Boom_7(ParticleEffectData("particle/boom 7.p")),
        Boom_8(ParticleEffectData("particle/boom 8.p")),
    }

    data class ParticleEffectData(val path: String) {
        lateinit var effect: ParticleEffect
    }

}