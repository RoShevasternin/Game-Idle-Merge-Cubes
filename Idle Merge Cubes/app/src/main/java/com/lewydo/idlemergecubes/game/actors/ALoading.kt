package com.lewydo.idlemergecubes.game.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.progress.AProgressLoader
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import kotlin.math.roundToInt

class ALoading(override val screen: AdvancedScreen): AdvancedGroup() {

    private val textLoading = "Loading"

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + textLoading + ".%").setSize(100)
    private val font      = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter)

    private val aLightProgressImg    = Image(gdxGame.assetsLoader.light_progress)
    private val aLoadingLbl          = Label("$textLoading...", Label.LabelStyle(font, Color.WHITE))
    private val aProgressFrameImg    = Image(gdxGame.assetsLoader.progress_frame)
    private val aProgressLoader      = AProgressLoader(screen)

    override fun addActorsOnGroup() {
        addLightProgressImg()
        addLoadingLbl()
        addProgressFrameImg()
        addProgressLoaderGroup()
    }

    // Actors ------------------------------------------------------------------------

    private fun addLightProgressImg() {
        addAndFillActor(aLightProgressImg)
    }

    private fun addLoadingLbl() {
        addActor(aLoadingLbl)
        aLoadingLbl.setBounds(397f, 258f, 666f, 136f)
        aLoadingLbl.setAlignment(Align.center)
    }

    private fun addProgressFrameImg() {
        addActor(aProgressFrameImg)
        aProgressFrameImg.setBounds(23f, 124f, 1396f, 140f)
    }

    private fun addProgressLoaderGroup() {
        addActor(aProgressLoader)
        aProgressLoader.setBounds(91f, 150f, 1278f, 73f)
    }

    // Logic ------------------------------------------------------------------------

    fun setProgressPercent(percent: Float) {
        aLoadingLbl.setText("$textLoading ${percent.roundToInt()}%")
        aProgressLoader.setProgressPercent(percent)
    }

}