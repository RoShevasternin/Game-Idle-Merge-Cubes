package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

open class ABuyButton(override val screen: AdvancedScreen) : AButton(screen, Type.BUY) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "BUY")
        .setShadow(7, 7, GameColor.brown_8D3800)
        .setBorder(2f, GameColor.brown_8D3800)
    private val font153 = screen.fontGenerator_Nunito_Black.generateFont(parameter.setSize(153))
    private val font114 = screen.fontGenerator_Nunito_Black.generateFont(parameter.setSize(114))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val buyLbl   = Label("BUY", Label.LabelStyle(font153, Color.WHITE))
    private val priceLbl = Label("0", Label.LabelStyle(font114, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLbls()

        collectBuyState()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLbls() {
        addActors(buyLbl, priceLbl)

        buyLbl.setBounds(250f, 67f, 329f, 209f)
        priceLbl.setBounds(712f, 93f, 143f, 157f)

        buyLbl.disable()
        buyLbl.setAlignment(Align.center)

        priceLbl.disable()
        priceLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectBuyState() {
        coroutine?.launch {
            combine(
                gdxGame.modelPlayer.coinsFlow,
                gdxGame.modelPlayer.buyPriceFlow,
                gdxGame.modelGrid.gridFlow
            ) { coins, price, grid ->

                val hasMoney = coins >= price
                val hasSpace = grid.any { it == 0 }

                Triple(price, hasMoney, hasSpace)
            }.collect { (price, hasMoney, hasSpace) ->
                    runGDX {
                        // оновлюємо ціну
                        priceLbl.setText(NumberFormatter.format(price))

                        // стан кнопки
                        if (hasMoney && hasSpace) enable() else disable()
                    }
                }
        }
    }

}