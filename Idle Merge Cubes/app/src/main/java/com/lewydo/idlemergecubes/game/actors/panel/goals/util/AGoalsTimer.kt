package com.lewydo.idlemergecubes.game.actors.panel.goals.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AGoalsTimer(
    override val screen: AdvancedScreen,
    style: MsdfStyle,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image()
    private val aTimerImg = Image(gdxGame.assetsAll.icon_timer)
    private val aLbl      = AMsdfLabel("", style)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBg()
        addTimerImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBg() {
        add(aBgImg) { fillParent() }
    }

    private fun addTimerImg() {
        aTimerImg.setSize(66f, 66f)
        add(aTimerImg) { startToStart(margin = 48f); centerY() }
    }

    private fun addLbl() {
        aLbl.setSize(1f, 76f)
        add(aLbl) { startToEnd(aTimerImg, 12f); centerY() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(text: String) {
        aLbl.setText(text)
        aLbl.pack()

        width = (48f + aTimerImg.width + 12f + aLbl.width + 48f)
    }

    fun setBg(ninePatch: NinePatch) {
       aBgImg.drawable = NinePatchDrawable(ninePatch)
    }

    fun setTextColor(color: Color) {
        aLbl.setTextColor(color)
    }

}