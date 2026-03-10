package com.lewydo.idlemergecubes.game.actors.panelGrid

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.controller.GridController
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class APanelGrid(override val screen: AdvancedScreen): AdvancedGroup() {

//    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + ",").setSize(83).setShadow(7, 7, GameColor.purple_350080)
//    private val font      = screen.fontGenerator_Nunito_Bold.generateFont(parameter)
//
//    private val aCoinLbl = Label("", Label.LabelStyle(font, GameColor.yellow_FFF858))

    // Actors
    private val aPanelGameImg = Image(gdxGame.assetsAll.PANEL_GAME)
    private val aGlare1Img    = Image(gdxGame.assetsAll.listGlarePanelGame[0])
    private val aGlare2Img    = Image(gdxGame.assetsAll.listGlarePanelGame[1])

    private val aGlareGamePanel1 = AGlareGridPanel(screen)
    private val aGlareGamePanel2 = AGlareGridPanel(screen)

    private val aCellLayer = ACellLayer(screen)
    private val aCubeLayer = ACubeLayer(screen)

    // Field
    private val controller = GridController(coroutine, gdxGame.modelGrid, gdxGame.modelPlayer, aCellLayer, aCubeLayer)


    override fun addActorsOnGroup() {
        addPanelGameImg()
        addCellsLayer()
        addGlares()

        addCubeLayer()

        controller.initialize()
    }

    // Actors ------------------------------------------------------------------------

    private fun addPanelGameImg() {
        addAndFillActor(aPanelGameImg)
    }

    private fun addGlares() {
        addActors(aGlare1Img, aGlare2Img)
        addActors(aGlareGamePanel1, aGlareGamePanel2)

        aGlare1Img.setBounds(116f, 1823f, 137f, 62f)
        aGlare2Img.setBounds(286f, 1859f, 56f, 17f)

        aGlareGamePanel1.setBounds(1714f, 1701f, 190f, 203f)
        aGlareGamePanel2.setBounds(0f, 12f, 190f, 203f)

        // Встановлюємо початкову прозорість, щоб не було різкої появи
        aGlare1Img.color.a = 0.4f
        aGlare2Img.color.a = 0.3f

        // Встановлюємо центр для масштабування
        aGlare1Img.setOrigin(Align.center)
        aGlare2Img.setOrigin(Align.center)

        aGlare1Img.disable()
        aGlare2Img.disable()

        animGlares()
    }

    private fun addCellsLayer() {
        addAndFillActor(aCellLayer)
    }

    private fun addCubeLayer() {
        addAndFillActor(aCubeLayer)
    }

    // Anim ------------------------------------------------------------------------

    private fun animGlares() {
        // Анімація для першого великого бліка
        aGlare1Img.addAction(Actions.forever(Actions.sequence(
            Actions.parallel(
                Actions.alpha(1f, 3f, Interpolation.sine),            // Стає яскравішим
                Actions.moveBy(15f, 5f, 3f, Interpolation.sine),      // Трохи "пливе" вбік
                Actions.scaleTo(0.6f, 0.5f, 5f, Interpolation.sine) // Ледь збільшується
            ),
            Actions.parallel(
                Actions.alpha(0.4f, 4f, Interpolation.sine),         // Затухає
                Actions.moveBy(-15f, -5f, 4f, Interpolation.sine),   // Повертається назад
                Actions.scaleTo(1f, 1f, 7f, Interpolation.sine)      // Повертає розмір
            )
        )))

        // Анімація для другого маленького бліка (робимо інший час, щоб не було синхронно)
        aGlare2Img.addAction(Actions.forever(Actions.sequence(
            Actions.delay(1f), // Невелика затримка для асиметрії
            Actions.parallel(
                Actions.alpha(0.8f, 2.5f, Interpolation.pow2In),    // Яскравість
                Actions.moveBy(20f, 3f, 2.5f, Interpolation.sine),  // Рух в іншу сторону
                Actions.scaleTo(0.8f, 0.6f, 8f, Interpolation.sine) // Ледь збільшується
            ),
            Actions.parallel(
                Actions.alpha(0.2f, 3.5f, Interpolation.pow2Out),
                Actions.moveBy(-20f, -3f, 3.5f, Interpolation.sine),
                Actions.scaleTo(1f, 1f, 6f, Interpolation.sine),
            )
        )))
    }

    // ======================================================
    // BUY
    // ======================================================

    fun buyCube() {
        controller.buyCube()
    }


}