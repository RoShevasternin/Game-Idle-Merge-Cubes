package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.button.AButton
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

open class ALeaderboardButton(override val screen: AdvancedScreen) : AButton(screen, Type.MENU_ITEM) {

    private val textLeaderboard = "Leaderboard"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(textLeaderboard)
    private val font = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(80))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aIconImg  = Image(gdxGame.assetsAll.menu_icon_leaderboard)
    private val aTitleLbl = Label(textLeaderboard, Label.LabelStyle(font, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        addIconImg()
        addTitleLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addIconImg() {
        addActor(aIconImg)
        aIconImg.setBounds(80f, 73f, 130f, 130f)
        aIconImg.disable()
    }

    private fun addTitleLbl() {
        addActor(aTitleLbl)
        aTitleLbl.setBounds(234f, 83f, 462f, 109f)
        aTitleLbl.disable()
    }

}