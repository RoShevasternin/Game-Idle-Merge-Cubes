package com.lewydo.rozval.game.actors.mainPanel

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.lewydo.rozval.game.actors.button.AButton
import com.lewydo.rozval.game.actors.counter.ALvLCounter
import com.lewydo.rozval.game.screens.MenuScreen
import com.lewydo.rozval.game.utils.GameColor
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.font.FontParameter

class GameMainPanel(_screen: AdvancedScreen): AbstractMainPanel(_screen) {

    override val isBackground = true

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font40    = screen.fontGenerator_LondrinaSolid_Regular.generateFont(parameter.setSize(40))

    private val labelStyle40 = LabelStyle(font40, GameColor.white)

    private val lvlCounter = ALvLCounter(screen, labelStyle40, MenuScreen.LVL_CLICK)
    private val menuBtn    = AButton(screen, AButton.Type.Menu)

    var menuBtnBlock = {}

    override fun AdvancedGroup.addActorsOnParentGroup() {
        addLvL()
        addMenu()
    }

    // Actors ------------------------------------------------------------------------

    private fun AdvancedGroup.addLvL() {
        addActor(lvlCounter)
        lvlCounter.setBounds(15f, 1008f, 140f, 47f)
    }

    private fun AdvancedGroup.addMenu() {
        addActor(menuBtn)
        menuBtn.apply {
            setBounds(18f, 35f, 124f, 51f)
            setOnClickListener { menuBtnBlock.invoke() }
        }
    }

}