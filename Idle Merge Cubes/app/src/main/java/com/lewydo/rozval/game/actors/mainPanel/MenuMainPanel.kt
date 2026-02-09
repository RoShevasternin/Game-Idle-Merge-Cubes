package com.lewydo.rozval.game.actors.mainPanel

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.lewydo.rozval.game.actors.button.AButton
import com.lewydo.rozval.game.actors.counter.AKeyCounter
import com.lewydo.rozval.game.actors.counter.AStarCounter
import com.lewydo.rozval.game.utils.GameColor
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.font.FontParameter

class MenuMainPanel(_screen: AdvancedScreen): AbstractMainPanel(_screen) {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font40    = screen.fontGenerator_LondrinaSolid_Regular.generateFont(parameter.setSize(40))

    private val labelStyle40 = LabelStyle(font40, GameColor.white)

    private val exitBtn      = AButton(screen, AButton.Type.Exit)
    private val getKeysBtn   = AButton(screen, AButton.Type.GetKeys)
    private val marketBtn    = AButton(screen, AButton.Type.Market)
    private val starCounter  = AStarCounter(screen, labelStyle40)
    private val keyCounter   = AKeyCounter(screen, labelStyle40)

    var exitBtnBlock    = {}
    var getKeysBtnBlock = {}
    var marketBtnBlock  = {}

    override fun AdvancedGroup.addActorsOnParentGroup() {
        addExitBtn()
        addGetKeysBtn()
        addMarketBtn()
        addStar()
        addKey()
    }

    // Actors ------------------------------------------------------------------------

    private fun AdvancedGroup.addExitBtn() {
        addActor(exitBtn)
        exitBtn.apply {
            setBounds(18f, 35f, 124f, 51f)
            setOnClickListener { exitBtnBlock() }
        }
    }

    private fun AdvancedGroup.addGetKeysBtn() {
        addActor(getKeysBtn)
        getKeysBtn.apply {
            setBounds(5f, 739f, 150f, 116f)
            setOnClickListener { getKeysBtnBlock() }
        }
    }

    private fun AdvancedGroup.addMarketBtn() {
        addActor(marketBtn)
        marketBtn.apply {
            setBounds(5f, 603f, 150f, 116f)
            setOnClickListener { marketBtnBlock() }
        }
    }

    private fun AdvancedGroup.addStar() {
        addActor(starCounter)
        starCounter.setBounds(15f, 1008f, 140f, 47f)
    }

    private fun AdvancedGroup.addKey() {
        addActor(keyCounter)
        keyCounter.setBounds(15f, 941f, 140f, 47f)
    }

}