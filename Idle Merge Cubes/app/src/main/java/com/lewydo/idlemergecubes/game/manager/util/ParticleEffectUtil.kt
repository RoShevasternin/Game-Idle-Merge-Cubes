package com.lewydo.idlemergecubes.game.manager.util

import com.lewydo.idlemergecubes.game.manager.ParticleEffectManager

class ParticleEffectUtil {

    class Loader {
        val LOADER = ParticleEffectManager.EnumParticleEffect.LOADER.data.effect
    }

    class All {
        val CONFETTI = ParticleEffectManager.EnumParticleEffect.CONFETTI.data.effect

        val BUY          = ParticleEffectManager.EnumParticleEffect.BUY.data.effect
        val STAR         = ParticleEffectManager.EnumParticleEffect.STAR.data.effect
        val WAVE_UPGRADE = ParticleEffectManager.EnumParticleEffect.WAVE_UPGRADE.data.effect

        val IDLE_CONFETTI = ParticleEffectManager.EnumParticleEffect.IDLE_CONFETTI.data.effect
        val IDLE_WAVE     = ParticleEffectManager.EnumParticleEffect.IDLE_WAVE.data.effect

        val CUBE = ParticleEffectManager.EnumParticleEffect.CUBE.data.effect

        val COLLECT = ParticleEffectManager.EnumParticleEffect.COLLECT.data.effect
    }
}

