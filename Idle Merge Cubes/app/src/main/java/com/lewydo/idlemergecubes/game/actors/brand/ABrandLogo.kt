package com.lewydo.idlemergecubes.game.actors.brand

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.screens.BrandScreen
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ABrandLogo(override val screen: BrandScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val aBrandBack  = Image(gdxGame.assetsBrand.brand_back)
    val aBrandFront = Image(gdxGame.assetsBrand.brand_front)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBrandBackImg()
        addBrandFrontImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBrandBackImg() {
        addAndFillActor(aBrandBack)
    }

    private fun addBrandFrontImg() {
        aBrandFront.setSize(672f, 672f)
        addActorAligned(aBrandFront, AlignH.CENTER, AlignV.CENTER)
    }

}