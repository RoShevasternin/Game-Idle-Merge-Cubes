package com.lewydo.idlemergecubes.game.actors.panel.goals.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setSize
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AGoalsReward(
    override val screen: AdvancedScreen,
    labelStyle: Label.LabelStyle,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(gdxGame.assetsAll.goals_pill_reward)
    private val aCoinImg = Image(gdxGame.assetsAll.coin)
    private val aLbl     = Label("", labelStyle)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBg()
        addCoinImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBg() {
        add(aBgImg) { fillParent() }
    }

    private fun addCoinImg() {
        aCoinImg.setSize(66f, 66f)
        add(aCoinImg) { startToStart(margin = 48f); centerY() }
    }

    private fun addLbl() {
        aLbl.setSize(1f, 76f)
        add(aLbl) { startToEnd(aCoinImg, 12f); centerY() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(text: String) {
        aLbl.setText(text)
        aLbl.pack()

        width = (48f + aCoinImg.width + 12f + aLbl.width + 48f)
    }

}