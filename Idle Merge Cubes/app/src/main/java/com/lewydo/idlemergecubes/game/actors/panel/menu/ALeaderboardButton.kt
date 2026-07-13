package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

open class ALeaderboardButton(override val screen: AdvancedScreen) : AButtonTexture(screen, AButtonStyles.Texture.MENU_ITEM) {

    private val textLeaderboard = "Leaderboard"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(textLeaderboard).setSize(80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aIconImg  = Image(gdxGame.assetsAll.menu_icon_leaderboard)
    private val aTitleLbl = Label(textLeaderboard, FontFactory.create(screen, parameter, screen.fontGenerator_Nunito_SemiBold))

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