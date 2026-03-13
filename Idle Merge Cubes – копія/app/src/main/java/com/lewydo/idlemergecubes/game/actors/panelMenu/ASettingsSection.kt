package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

open class ASettingsSection(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font80 = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(80))
    private val font72 = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(72))

    private val ls72 = Label.LabelStyle(font72, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image(gdxGame.assetsAll.panel_settings)
    private val aIconImg   = Image(gdxGame.assetsAll.menu_icon_settings)
    private val aTitleLbl  = Label("Settings", Label.LabelStyle(font80, Color.WHITE))
    private val aExpandImg = Image(gdxGame.assetsAll.expand)

    private val aSettItemSound = ASettingsItem(screen, ls72, ASettingsItem.Type.SOUND)
    private val aSettItemMusic = ASettingsItem(screen, ls72, ASettingsItem.Type.MUSIC)
    private val aSettItemVibro = ASettingsItem(screen, ls72, ASettingsItem.Type.VIBRO)
    private val aSettItemAlarm = ASettingsItem(screen, ls72, ASettingsItem.Type.ALARM)
    private val aSettItemInfo  = ASettingsItem(screen, ls72, ASettingsItem.Type.INFO)

    private val aVersionLbl  = Label("Game Version: ${BuildConfig.VERSION_NAME}", Label.LabelStyle(font72, GameColor.white_55))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onHeightChanged: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBgImg)
        addIconImg()
        addTitleLbl()
        addExpandImg()
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
        aTitleLbl.setBounds(234f, 83f, 303f, 109f)
        aTitleLbl.disable()
    }

    private fun addExpandImg() {
        addActor(aExpandImg)
        aExpandImg.setBounds(1754f, 96f, 82f, 82f)
        aExpandImg.disable()
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