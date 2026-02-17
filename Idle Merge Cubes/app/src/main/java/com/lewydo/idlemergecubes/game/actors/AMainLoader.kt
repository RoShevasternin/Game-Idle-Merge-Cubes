package com.lewydo.idlemergecubes.game.actors

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.light.ALightLoader
import com.lewydo.idlemergecubes.game.screens.LoaderScreen
import com.lewydo.idlemergecubes.game.utils.Acts
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AMainLoader(override val screen: LoaderScreen): AdvancedGroup() {

    private val aEffectLoader = AParticleEffectActor(ParticleEffect(gdxGame.particleEffectUtil.Loader))

    val aLightLoader = ALightLoader(screen)
    val cubeImg      = Image(gdxGame.assetsLoader.cube)
    val aLoading     = ALoading(screen)

    override fun addActorsOnGroup() {
        addALightLoader()
        addAEffectLoader()
        addCubeImg()
        addALoading()
    }

    // Actors ------------------------------------------------------------------------

    private fun addALightLoader() {
        addActor(aLightLoader)
        aLightLoader.setBounds(-24f, 707f, 2208f, 2425f)
    }

    private fun addAEffectLoader() {
        addActor(aEffectLoader)
        val halfSize = 480f / 2f
        aEffectLoader.setBounds(843f + halfSize, 1679f + halfSize, 480f, 480f)
        aEffectLoader.start()
    }

    private fun addCubeImg() {
        addActor(cubeImg)
        cubeImg.apply {
            setBounds(712f, 1511f, 742f, 771f)
            setOrigin(Align.center)

            // Загальний темп — СПОКІЙНИЙ
            val baseTime = 3.6f

            addAction(
                Acts.forever(
                    Acts.parallel(

                        // 1. ДИХАННЯ (основа)
                        Acts.sequence(
                            Acts.scaleTo(1.06f, 1.06f, baseTime * 0.5f, Interpolation.sine),
                            Acts.scaleTo(1f, 1f,     baseTime * 0.5f, Interpolation.sine)
                        ),

                        // 2. ЛЕГКИЙ НАХИЛ (енергія)
                        Acts.sequence(
                            Acts.rotateTo(3.5f,  baseTime * 0.5f, Interpolation.sine),
                            Acts.rotateTo(-3.5f, baseTime * 0.5f, Interpolation.sine)
                        ),

                        // 3. МʼЯКА ЛЕВІТАЦІЯ
                        Acts.sequence(
                            Acts.moveBy(0f, 10f,  baseTime * 0.5f, Interpolation.sine),
                            Acts.moveBy(0f, -10f, baseTime * 0.5f, Interpolation.sine)
                        )
                    )
                )
            )
        }
    }

    private fun addALoading() {
        addActor(aLoading)
        aLoading.setBounds(350f, 826f, 1460f, 587f)
    }

}