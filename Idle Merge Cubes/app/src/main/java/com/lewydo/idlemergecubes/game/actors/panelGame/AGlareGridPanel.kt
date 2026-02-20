package com.lewydo.idlemergecubes.game.actors.panelGame

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.Acts
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AGlareGridPanel(override val screen: AdvancedScreen): AdvancedGroup() {

    private val aGlare3Img = Image(gdxGame.assetsAll.listGlarePanelGame[2])
    private val aGlare4Img = Image(gdxGame.assetsAll.listGlarePanelGame[3])

    override fun addActorsOnGroup() {
        addGlares()

        animGlares()
    }

    // Actors ------------------------------------------------------------------------

    private fun addGlares() {
        addActors(aGlare3Img, aGlare4Img)
        aGlare3Img.setBounds(-2f, 42f, 161f, 161f)
        aGlare4Img.setBounds(2f, 0f, 188f, 188f)

        aGlare3Img.addAction(Acts.alpha(0f))
        aGlare4Img.addAction(Acts.alpha(0f))
        aGlare3Img.setScale(0f)
        aGlare4Img.setScale(0f)

        aGlare3Img.setOrigin(Align.center)
        aGlare4Img.setOrigin(Align.center)

        aGlare3Img.disable()
        aGlare4Img.disable()
    }

    // Anim ------------------------------------------------------------------------

    private fun animGlares() {
        animStar(aGlare3Img)
        animLens(aGlare4Img)
    }

    private fun animStar(img: Image) {
        // Генеруємо нові рандомні значення для КОЖНОГО циклу
        val time   = MathUtils.random(2f, 4f)
        val angle  = MathUtils.random(-360f, 360f)
        val scale  = MathUtils.random(0.7f, 1.3f)

        img.addAction(Acts.sequence(
            Acts.parallel(
                Acts.rotateBy(angle, time, Interpolation.sine),
                Acts.scaleTo(scale, scale, time, Interpolation.sine),
                Acts.alpha(MathUtils.random(0.2f, 0.9f), time, Interpolation.sine)
            ),
            Acts.parallel(
                Acts.rotateBy(angle, time, Interpolation.sine),
                Acts.scaleTo(0f, 0f, time, Interpolation.sine),
                Acts.alpha(0f, time, Interpolation.sine)
            ),
            // В кінці викликаємо цей же метод — і все піде по новому колу з новими цифрами
            Acts.run { animStar(img) }
        ))
    }

    private fun animLens(img: Image) {
        // Нові рандомні параметри для кожного циклу
        val time   = MathUtils.random(3f, 6f)    // Блік має бути повільнішим за зірку
        val scale  = MathUtils.random(0.8f, 1.5f)
        val alpha  = MathUtils.random(0.3f, 0.7f)

        // Лівітація: невелике зміщення від початкової позиції
        val moveX  = MathUtils.random(-30f, 30f)
        val moveY  = MathUtils.random(-20f, 20f)

        img.addAction(Acts.sequence(
            // Фаза 1: Плавне проявлення, рух в точку і збільшення
            Acts.parallel(
                Acts.moveBy(moveX, moveY, time, Interpolation.sine),
                Acts.scaleTo(scale, scale, time, Interpolation.sine),
                Acts.alpha(alpha, time, Interpolation.sine)
            ),
            // Фаза 2: Повернення назад, зменшення і повне зникнення
            Acts.parallel(
                Acts.moveBy(-moveX, -moveY, time, Interpolation.sine),
                Acts.scaleTo(0f, 0f, time, Interpolation.sine),
                Acts.alpha(0f, time, Interpolation.sine)
            ),
            // Рекурсія: запускаємо новий рандомний цикл
            Acts.run { animLens(img) }
        ))
    }

    // Logic ------------------------------------------------------------------------


}