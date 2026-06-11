package com.lewydo.idlemergecubes.game.actors.panel.menu.settings

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.screens.WhoScreen
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.animHideAndDisable
import com.lewydo.idlemergecubes.game.utils.actor.animRotateTo
import com.lewydo.idlemergecubes.game.utils.actor.animShowAndEnable
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ASettingsSection(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image(gdxGame.assetsAll.panel_settings)
    private val aTopGroup  = ATmpGroup(screen)
    private val aIconImg   = Image(gdxGame.assetsAll.menu_icon_settings)
    private val aTitleLbl  = Label("Settings", FontFactory.create(screen, parameter, screen.fontGenerator_Nunito_SemiBold))
    private val aExpandImg = Image(gdxGame.assetsAll.expand)

    private val aSettingsContent = ASettingsContent(screen)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    var isOpen = false

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeAnim = 0.25f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addTopGroup()
        addContent()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    // ------------------------------------------------------------------------
    // Add Actors - TopGroup
    // ------------------------------------------------------------------------

    private fun addTopGroup() {
        aTopGroup.setSize(width, height)
        add(aTopGroup) { topToTop() }

        aTopGroup.addActors(aIconImg, aTitleLbl, aExpandImg)
        setUpIconImg(); setUpTitleLbl(); setUpExpandImg();

        aTopGroup.setOnClickListener { toggle() }
    }

    private fun setUpIconImg() {
        aIconImg.setBounds(80f, 72f, 130f, 130f)
        aIconImg.disable()
    }

    private fun setUpTitleLbl() {
        aTitleLbl.setBounds(234f, 82f, 303f, 109f)
        aTitleLbl.disable()
    }

    private fun setUpExpandImg() {
        aExpandImg.setBounds(1754f, 96f, 82f, 82f)
        aExpandImg.disable()
        aExpandImg.setOrigin(Align.center)
    }

    // ------------------------------------------------------------------------
    // Add Actors - Content
    // ------------------------------------------------------------------------

    private fun addContent() {
        aSettingsContent.animHideAndDisable()

        addActor(aSettingsContent)
        aSettingsContent.setBounds(76f, 64f, 1764f, 1218f)

        aSettingsContent.setInitStateBox(
            sound = gdxGame.settings.IS_SOUND,
            music = gdxGame.settings.IS_MUSIC,
            vibro = gdxGame.settings.IS_VIBRO,
            alarm = gdxGame.settings.IS_ALARM,
        )

        aSettingsContent.handleSettingsContent()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun toggle() {
        if (isOpen) animClose() else animOpen()
        isOpen = !isOpen
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animOpen() {
        clearActions()
        addAction(Actions.sequence(
            Actions.sizeTo(width, 1530f, timeAnim, Interpolation.sineOut),
            Actions.run { aSettingsContent.animShowAndEnable(0.10f) }
        ))

        aExpandImg.animRotateTo(180f, timeAnim)
    }

    private fun animClose() {
        clearActions()
        addAction(Actions.sequence(
            Actions.run { aSettingsContent.animHideAndDisable(0.10f) },
            Actions.sizeTo(width, 275f, timeAnim, Interpolation.sineIn),
        ))

        aExpandImg.animRotateTo(0f, timeAnim)
    }

    // ------------------------------------------------------------------------
    // Handler Util
    // ------------------------------------------------------------------------
    private fun ASettingsContent.handleSettingsContent() {
        onSoundBlock = { isOn -> gdxGame.settings.IS_SOUND = isOn }
        onMusicBlock = { isOn -> gdxGame.settings.IS_MUSIC = isOn }
        onVibroBlock = { isOn -> gdxGame.settings.IS_VIBRO = isOn }
        onAlarmBlock = { isOn -> gdxGame.settings.IS_ALARM = isOn }

        onInfoBlock = {
            screen.animHideScreen { gdxGame.navigationManager.navigate(WhoScreen::class.java.name, screen::class.java.name) }
        }
    }

}