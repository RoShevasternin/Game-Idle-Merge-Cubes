package com.lewydo.idlemergecubes.game.actors.brand

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.screens.BrandScreen
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AMainBrand(override val screen: BrandScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val aBrandLogo = ABrandLogo(screen)
    val aLewydoImg = Image(gdxGame.assetsBrand.lewydo)
    val aSloganImg = Image(gdxGame.assetsBrand.slogan)
    val aBrandLine = Image(gdxGame.assetsBrand.brand_line)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBrandLogo()
        addLewydoImg()
        addSloganImg()
        addBrandLineImg()

        // Всі актори починають невидимими
        aBrandLogo.color.a = 0f
        aLewydoImg.color.a = 0f
        aSloganImg.color.a = 0f
        aBrandLine.color.a = 0f
        aBrandLine.setScale(0f, 1f)
        aBrandLine.setOrigin(Align.center)
        aLewydoImg.setOrigin(Align.center)
        aLewydoImg.setScale(1.06f)
        aSloganImg.setOrigin(Align.center)
        aSloganImg.setScale(1.06f)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBrandLogo() {
        addActor(aBrandLogo)
        aBrandLogo.setBounds(580f, 1698f, 1000f, 1000f)
    }

    private fun addLewydoImg() {
        addActor(aLewydoImg)
        aLewydoImg.setBounds(580f, 1343f, 1000f, 355f)
    }

    private fun addSloganImg() {
        addActor(aSloganImg)
        aSloganImg.setBounds(580f, 1253f, 1000f, 90f)
    }

    private fun addBrandLineImg() {
        addActor(aBrandLine)
        aBrandLine.setBounds(730f, 1145f, 700f, 9f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    fun playIntroAnimation(onComplete: () -> Unit) {
        // 1. Логотип — fade in (0.3 → 1.1)
        aBrandLogo.addAction(
            Actions.sequence(
                Actions.delay(0.3f),
                Actions.fadeIn(0.8f, Interpolation.fade)
            )
        )

        // 2. Lewydo — розфокус → фокус (0.9 → 1.5)
        aLewydoImg.addAction(
            Actions.sequence(
                Actions.delay(0.9f),
                Actions.parallel(
                    Actions.fadeIn(0.6f, Interpolation.fade),
                    Actions.scaleTo(1f, 1f, 0.6f, Interpolation.fade)
                )
            )
        )

        // 3. Slogan — розфокус → фокус (1.3 → 1.9)
        aSloganImg.addAction(
            Actions.sequence(
                Actions.delay(1.3f),
                Actions.parallel(
                    Actions.fadeIn(0.6f, Interpolation.fade),
                    Actions.scaleTo(1f, 1f, 0.6f, Interpolation.fade)
                )
            )
        )

        // 4. Brand line — розкривається (1.7 → 2.4)
        // + callback після завершення
        aBrandLine.addAction(
            Actions.sequence(
                Actions.delay(1.7f),
                Actions.parallel(
                    Actions.fadeIn(0.3f),
                    Actions.scaleTo(1f, 1f, 0.7f, Interpolation.exp5Out)
                ),
                // Пауза щоб гравець побачив повну картину
                Actions.delay(0.6f),
                Actions.run { onComplete() }
            )
        )
    }

}