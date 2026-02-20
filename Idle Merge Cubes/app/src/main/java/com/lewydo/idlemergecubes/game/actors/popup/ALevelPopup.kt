package com.lewydo.idlemergecubes.game.actors.popup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.progress.AProgressPopupXP
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.launch

class ALevelPopup(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "XP:/,")
    private val fontXP    = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(72))

    private val aPopupImg = Image(gdxGame.assetsAll.dialog_lvl)
    private val aXPLbl    = Label("XP: 0/0", Label.LabelStyle(fontXP, Color.WHITE))
    private val aProgress = AProgressPopupXP(screen)

    override fun addActorsOnGroup() {
        addPopupImg()
        addXPLbl()
        addProgress()

        collectPlayerData()
    }

    // Actors ------------------------------------------------------------------------

    private fun addPopupImg() {
        addAndFillActor(aPopupImg)
    }

    private fun addXPLbl() {
        addActor(aXPLbl)
        aXPLbl.setBounds(82f, 127f, 533f, 98f)
    }

    private fun addProgress() {
        addActor(aProgress)
        aProgress.setBounds(82f, 67f, 533f, 44f)
    }

    // Logic ------------------------------------------------------------------------

    private fun collectPlayerData() {
        coroutine?.launch {
            launch {
                gdxGame.modelPlayer.xpFlow.collect {
                    runGDX {
                        val currentXP = NumberFormatter.format(gdxGame.modelPlayer.xpIntoCurrentLevel())
                        val nextXP    = NumberFormatter.format(gdxGame.modelPlayer.xpToNextLevel())
                        aXPLbl.setText("XP: $currentXP / $nextXP")
                        calculateAndUpdateWidth()

                        aProgress.progressPercentFlow.value = gdxGame.modelPlayer.progressPercent100()
                    }
                }
            }
        }
    }

    private fun calculateAndUpdateWidth() {
        aXPLbl.pack()
        if (aXPLbl.width >= 533f) {

            val paddingLeft = 82f
            val paddingRight = 82f

            val newPanelWidth    = paddingLeft + aXPLbl.width + paddingRight
            //val newProgressWidth = aXPLbl.width

            aPopupImg.setSize(newPanelWidth, aPopupImg.height)
            //aProgress.setSize(newProgressWidth, aProgress.height)
        }
    }

}