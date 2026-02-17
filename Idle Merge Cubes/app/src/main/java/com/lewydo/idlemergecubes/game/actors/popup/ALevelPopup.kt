package com.lewydo.idlemergecubes.game.actors.popup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.actors.progress.AProgressPopupXP
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import kotlinx.coroutines.launch

class ALevelPopup(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "XP:/")
    private val fontXP    = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(72))

    private val aPopupImg = Image(gdxGame.assetsAll.dialog_lvl)
    private val aXPLbl    = Label("XP: 0/0", Label.LabelStyle(fontXP, Color.WHITE))
    private val aProgress = AProgressPopupXP(screen)

    override fun addActorsOnGroup() {
        addPopupImg()
        addXPLbl()
        addProgress()
    }

    // Actors ------------------------------------------------------------------------

    private fun addPopupImg() {
        addAndFillActor(aPopupImg)
    }

    private fun addXPLbl() {
        addActor(aXPLbl)
        aXPLbl.setBounds(65f, 128f, 367f, 98f)
    }

    private fun addProgress() {
        addActor(aProgress)
        aProgress.setBounds(64f, 68f, 533f, 44f)
    }

    // Logic ------------------------------------------------------------------------

    private fun collectPlayerData() {
        coroutine?.launch {
            launch {
                gdxGame.modelPlayer.levelFlow.collect {

                }
            }
        }
    }

}