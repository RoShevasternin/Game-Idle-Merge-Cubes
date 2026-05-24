package com.lewydo.idlemergecubes.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.progress.AProgressPopupXP
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.launch

class ALevelPopup(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "XP:/,")
        .setSize(72)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPopupImg = Image(gdxGame.assetsAll.dialog_lvl)
    private val aXPLbl    = Label("XP: 0/0", FontFactory.create(screen, parameter, screen.fontGenerator_Nunito_SemiBold))
    private val aProgress = AProgressPopupXP(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addPopupImg()
        addXPLbl()
        addProgress()
        collectXp()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    private fun collectXp() {
        coroutine?.launch {
            gdxGame.modelPlayer.xpFlow.collect {
                runGDX { updateXpUI() }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun updateXpUI() {
        val model      = gdxGame.modelPlayer
        val currentXp  = NumberFormatter.format(model.xpInCurrentLevel())
        val neededXp   = NumberFormatter.format(model.xpForLevel())

        aXPLbl.setText("XP: $currentXp / $neededXp")
        aProgress.progressPercentFlow.value = model.levelProgress() * 100f

        adjustPopupWidth()
    }

    private fun adjustPopupWidth() {
        aXPLbl.pack()
        if (aXPLbl.width < 533f) return

        val newWidth = 82f + aXPLbl.width + 82f
        aPopupImg.setSize(newWidth, aPopupImg.height)
    }
}