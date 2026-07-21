package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

open class ALeaderboardButton(override val screen: AdvancedScreen) : AButtonTexture(screen, AButtonStyles.Texture.MENU_ITEM) {

    private val textLeaderboard = "Leaderboard"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoSemiBold, 80f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aIconImg  = Image(gdxGame.assetsAll.menu_icon_leaderboard)
    private val aTitleLbl = AMsdfLabel(textLeaderboard, styleDef)

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