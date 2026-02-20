package com.lewydo.idlemergecubes.game.actors.light

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.screens.LoaderScreen
import com.lewydo.idlemergecubes.game.utils.Acts
import com.lewydo.idlemergecubes.game.utils.actor.setBounds
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ALightLoader(override val screen: LoaderScreen): AdvancedGroup() {

    private val listBoundsData = listOf(
        Rectangle(0f, 0f, 2208f, 2425f),
        Rectangle(121f, 260f, 1893f, 1893f),
        Rectangle(430f, 578f, 1292f, 1292f),
        Rectangle(614f, 833f, 1323f, 1197f),
        Rectangle(935f, 1011f, 840f, 840f),
        Rectangle(934f, 1044f, 708f, 708f),
    )

    private val listLightImg = List(listBoundsData.size) { Image(gdxGame.assetsLoader.listLight[it]) }

    override fun addActorsOnGroup() {
        addListLightImg()
        
        animateLights()
    }

    // Actors ------------------------------------------------------------------------

    private fun addListLightImg() {
        listLightImg.forEachIndexed { index, image ->
            addActor(image)
            image.setBounds(listBoundsData[index])
        }
    }

    // Anim ------------------------------------------------------------------------

    private fun animateLights() {
        listLightImg.forEachIndexed { index, image ->
            image.setOrigin(Align.center)

            val ratio = index / (listLightImg.size - 1f)   // 0 → 1
            val isTop = index >= 3

            // ⏱ Загальний темп (короткий цикл!)
            val baseTime = 0.9f + (1f - ratio) * 0.4f      // 0.9 → 1.3 сек

            // 🌊 Хвиля масштабу (ГОЛОВНЕ)
            val scaleMax = 1f + ratio * 0.12f              // верхній ≈ +12%
            val scaleWave = Acts.forever(
                Acts.sequence(
                    Acts.delay(ratio * 0.12f), // фазовий зсув
                    Acts.scaleTo(scaleMax, scaleMax, baseTime * 0.45f, Interpolation.pow2Out),
                    Acts.scaleTo(1f, 1f,       baseTime * 0.55f, Interpolation.sine)
                )
            )

            // 🔄 Ротація (ТІЛЬКИ для верхніх)
            val rotation = if (isTop) {
                val dir = if (index % 2 == 0) 1f else -1f
                Acts.forever(
                    Acts.sequence(
                        Acts.rotateTo(5f * dir, baseTime * 0.65f, Interpolation.sine),
                        Acts.rotateTo(-5f * dir, baseTime * 0.65f, Interpolation.sine)
                    )
                )
            } else null

            // 🟣 Ледь помітний дрейф (тільки фон і середні)
            val drift = if (index <= 2) {
                val dist = 18f * (1f - ratio)
                Acts.forever(
                    Acts.sequence(
                        Acts.moveBy(dist, dist, baseTime, Interpolation.sine),
                        Acts.moveBy(-dist, -dist, baseTime, Interpolation.sine)
                    )
                )
            } else null

            // 🌈 Яскравість — ФІКСОВАНА (без блимання)
//            image.color.a = when (index) {
//                5 -> 1f      // жовтий
//                4 -> 0.95f
//                3 -> 0.9f
//                2 -> 0.8f
//                1 -> 0.65f
//                else -> 0.5f
//            }

            // ▶️ ЗАПУСК
            image.addAction(
                Acts.parallel(
                    scaleWave,
                    *(listOfNotNull(rotation, drift).toTypedArray())
                )
            )
        }
    }


    fun onLoaderFinish() {
        listLightImg.forEachIndexed { index, image ->
            // Розраховуємо силу для кожного шару
            val ratio = (index + 1f) / listLightImg.size

            image.addAction(
                Acts.sequence(
                    // 1. ПІДГОТОВКА: Світло на мить стискається і стає супер-яскравм
                    Acts.parallel(
                        Acts.scaleTo(0.7f, 0.7f, 0.12f, Interpolation.pow2In),
                        Acts.alpha(1f, 0.1f)
                    ),

                    // 2. ВИБУХ: Використовуємо експоненціальну інтерполяцію для швидкості
                    Acts.parallel(
                        // Головне сяйво розлітається дуже сильно
                        Acts.scaleTo(
                            5f + ratio * 3f,
                            5f + ratio * 3f,
                            2.0f,
                            Interpolation.exp10Out // Дуже різкий старт вибуху
                        ),

                        // Прозорість зникає плавно, щоб залишити післясвічення
                        Acts.alpha(0f, 0.5f, Interpolation.pow3Out),

                        // Додаємо фінальний оберт для динаміки
                        Acts.rotateBy(if (index % 2 == 0) 120f else -120f, 0.6f, Interpolation.exp5Out)
                    ),

                    // Видаляємо актора, щоб звільнити пам'ять після анімації
                    //Acts.removeActor()
                )
            )
        }
    }


}