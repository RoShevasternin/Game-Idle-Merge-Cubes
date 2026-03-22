package com.lewydo.idlemergecubes.game.actors.panel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelBalanceCoin(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + ",").setSize(83).setShadow(7, 7, GameColor.purple_350080)
    private val font      = screen.fontGenerator_Nunito_Bold.generateFont(parameter)

    private val aPanelCoinImg = Image(gdxGame.assetsAll.panel_coin)
    private val aCoinLbl      = Label("", Label.LabelStyle(font, GameColor.yellow_FFF858))
    private val aCoinImg      = Image(gdxGame.assetsAll.coin)

    override fun addActorsOnGroup() {
        addPanelCoinImg()
        addCoinLbl()
        addCoinImg()

        coroutine?.launch { collectCoin() }
    }

    // Actors ------------------------------------------------------------------------

    private fun addPanelCoinImg() {
        addActor(aPanelCoinImg)
        aPanelCoinImg.setBounds(59f, 0f, 482f, 175f)
    }

    private fun addCoinLbl() {
        addActor(aCoinLbl)
        aCoinLbl.setPosition(236f, 37f)
        aCoinLbl.setSize(aCoinLbl.prefWidth, 114f)
    }

    private fun addCoinImg() {
        addActor(aCoinImg)
        aCoinImg.setBounds(0f, 7f, 175f, 175f)
        aCoinImg.setOrigin(Align.center)

        startSimpleSway()
    }

    // Anim ------------------------------------------------------------------------

    private fun startSimpleSway() {
        val rotateRight = Actions.rotateTo(10f, 1.8f, Interpolation.sine)
        val rotateLeft  = Actions.rotateTo(-10f, 1.8f, Interpolation.sine)

        aCoinImg.addAction(
            Actions.forever(
                Actions.sequence(
                    rotateRight,
                    rotateLeft
                )
            )
        )
    }

    // Logic ------------------------------------------------------------------------

    private suspend fun collectCoin() {
        gdxGame.modelPlayer.coinsFlow.collect { coin -> runGDX {
            aCoinLbl.setText(NumberFormatter.format(coin))
            aCoinLbl.pack()

            if (aCoinLbl.width >= 270f) {

                val paddingLeft = 150f
                val paddingRight = 55f

                val newPanelWidth = paddingLeft + aCoinLbl.width + paddingRight
                aPanelCoinImg.setSize(newPanelWidth, aPanelCoinImg.height)
            }
        }
    } }


}