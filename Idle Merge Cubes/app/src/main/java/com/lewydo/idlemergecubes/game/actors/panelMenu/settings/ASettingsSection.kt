package com.lewydo.idlemergecubes.game.actors.panelMenu.settings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ASettingsSection(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font80 = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(80))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image(gdxGame.assetsAll.panel_settings)
    private val aIconImg   = Image(gdxGame.assetsAll.menu_icon_settings)
    private val aTitleLbl  = Label("Settings", Label.LabelStyle(font80, Color.WHITE))
    private val aExpandImg = Image(gdxGame.assetsAll.expand)

    private val aSettingsContent = ASettingsContent(screen)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onHeightChanged: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        debug()
        addAndFillActor(aBgImg)
        addIconImg()
        addTitleLbl()
        addExpandImg()
        addContent()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addIconImg() {
        aIconImg.setSize(130f, 130f)
        add(aIconImg) {
            startToStart(margin = 80f)
            topToTop(margin = 73f)
        }
        aIconImg.disable()
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(303f, 109f)
        add(aTitleLbl) {
            startToStart(margin = 234f)
            topToTop(margin = 83f)
        }
        aTitleLbl.disable()
    }

    private fun addExpandImg() {
        aExpandImg.setSize(82f, 82f)
        add(aExpandImg) {
            startToStart(margin = 1754f)
            topToTop(margin = 96f)
        }
        aExpandImg.disable()
    }

    private fun addContent() {
        aSettingsContent.apply {
            //color.a = 0f
            disable()
        }

        addActor(aSettingsContent)
        aSettingsContent.setBounds(76f, 64f, 1764f, 1218f)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animHeight(to: Float) {
        val from = height
        clearActions()
        addAction(object : Action() {
            private var t = 0f
            override fun act(delta: Float): Boolean {
                t      = (t + delta / 0.25f).coerceAtMost(1f)
                height = Interpolation.sineOut.apply(from, to, t)
                onHeightChanged.invoke()
                return t >= 1f
            }
        })
    }

}