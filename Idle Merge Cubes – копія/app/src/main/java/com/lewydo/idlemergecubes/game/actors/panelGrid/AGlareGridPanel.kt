package com.lewydo.idlemergecubes.game.actors.panelGrid

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.SizeScaler
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AGlareGridPanel(override val screen: AdvancedScreen): AdvancedGroup() {

    override val sizeScaler = SizeScaler(SizeScaler.Axis.X, 190f)

    private val aGlare3Img = Image(gdxGame.assetsAll.listGlarePanelGame[2])
    private val aGlare4Img = Image(gdxGame.assetsAll.listGlarePanelGame[3])

    override fun addActorsOnGroup() {
        addGlares()

        animGlares()
    }

    // Actors ------------------------------------------------------------------------

    private fun addGlares() {
        addActors(aGlare3Img, aGlare4Img)
        aGlare3Img.setBoundsScaled(-2f, 42f, 161f, 161f)
        aGlare4Img.setBoundsScaled(2f, 0f, 188f, 188f)

        aGlare3Img.addAction(Actions.alpha(0f))
        aGlare4Img.addAction(Actions.alpha(0f))
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

        img.addAction(Actions.sequence(
            Actions.parallel(
                Actions.rotateBy(angle, time, Interpolation.sine),
                Actions.scaleTo(scale, scale, time, Interpolation.sine),
                Actions.alpha(MathUtils.random(0.2f, 0.9f), time, Interpolation.sine)
            ),
            Actions.parallel(
                Actions.rotateBy(angle, time, Interpolation.sine),
                Actions.scaleTo(0f, 0f, time, Interpolation.sine),
                Actions.alpha(0f, time, Interpolation.sine)
            ),
            // В кінці викликаємо цей же метод — і все піде по новому колу з новими цифрами
            Actions.run { animStar(img) }
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

        img.addAction(Actions.sequence(
            // Фаза 1: Плавне проявлення, рух в точку і збільшення
            Actions.parallel(
                Actions.moveBy(moveX, moveY, time, Interpolation.sine),
                Actions.scaleTo(scale, scale, time, Interpolation.sine),
                Actions.alpha(alpha, time, Interpolation.sine)
            ),
            // Фаза 2: Повернення назад, зменшення і повне зникнення
            Actions.parallel(
                Actions.moveBy(-moveX, -moveY, time, Interpolation.sine),
                Actions.scaleTo(0f, 0f, time, Interpolation.sine),
                Actions.alpha(0f, time, Interpolation.sine)
            ),
            // Рекурсія: запускаємо новий рандомний цикл
            Actions.run { animLens(img) }
        ))
    }

    // Logic ------------------------------------------------------------------------


}