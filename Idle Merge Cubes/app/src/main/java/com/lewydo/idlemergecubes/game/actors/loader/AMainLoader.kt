package com.lewydo.idlemergecubes.game.actors.loader

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.screens.LoaderScreen
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.HEIGHT_UI
import com.lewydo.idlemergecubes.game.utils.WIDTH_UI
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class AMainLoader(override val screen: LoaderScreen): AConstraintLayout(screen) {

    private val textBranding = """
        Powered by LibGDX
        Developed by Lewydo™
        Version ${BuildConfig.VERSION_NAME}
    """.trimIndent()

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoSemiBold, 40f, GameColor.white_55)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val aCenterContentLoader = ACenterContentLoader(screen)
    val aBrandingLbl         = AMsdfLabel(textBranding, styleDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCenterContentLoader()
        addBrandingLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addCenterContentLoader() {
        aCenterContentLoader.setSize(WIDTH_UI, HEIGHT_UI)
        add(aCenterContentLoader) { center() }
    }

    private fun addBrandingLbl() {
        aBrandingLbl.setSize(443f, 165f)
        addActorAligned(aBrandingLbl, AlignH.CENTER)
        aBrandingLbl.setAlignment(Align.center)
        aBrandingLbl.y = 120f
    }

}