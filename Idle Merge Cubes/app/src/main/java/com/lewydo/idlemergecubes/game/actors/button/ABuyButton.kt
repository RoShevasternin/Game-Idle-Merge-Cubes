package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setBounds
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import kotlin.math.cos

open class ABuyButton(override val screen: AdvancedScreen) : AButton(screen, Type.Buy) {
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "BUY")
        .setShadow(7, 7, GameColor.brown_8D3800)
        .setBorder(2f, GameColor.brown_8D3800)
    private val font153 = screen.fontGenerator_Nunito_Black.generateFont(parameter.setSize(153))
    private val font114 = screen.fontGenerator_Nunito_Black.generateFont(parameter.setSize(114))

    private val buyLbl  = Label("BUY", Label.LabelStyle(font153, Color.WHITE))
    private val costLbl = Label("12", Label.LabelStyle(font114, Color.WHITE))

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addLbls()
    }

    // Actors ------------------------------------------------------------------------

    private fun addLbls() {
        addActors(buyLbl, costLbl)

        buyLbl.setBounds(250f, 67f, 329f, 209f)
        costLbl.setBounds(712f, 93f, 143f, 157f)

        buyLbl.disable()
        buyLbl.setAlignment(Align.center)

        costLbl.disable()
        costLbl.setAlignment(Align.center)
    }

}