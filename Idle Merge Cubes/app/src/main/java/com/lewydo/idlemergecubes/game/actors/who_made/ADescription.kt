package com.lewydo.idlemergecubes.game.actors.who_made

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.R
import com.lewydo.idlemergecubes.game.actors.AScrollPane
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.linear.AVerticalGroup
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.setOnTouchListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ADescription(override val screen: AdvancedScreen): AdvancedGroup() {

    private val appName    = gdxGame.activity.getString(R.string.app_name)
    private val appVersion = BuildConfig.VERSION_NAME
    private val subject    = "$appName | version: $appVersion"

    private val veldanEmail     = "veldan1202@gmail.com"
    private val veldanTelegram  = "vel_dan"
    private val veldanInstagram = "___vel__dan___"

    private val lilyEmail     = "Lilyadesign05@gmail.com"
    private val lilyTelegram  = "Over_lilya"
    private val lilyInstagram = "Lilya.design"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(
        screen = screen,
        gap    = 126f,
        alignH = AlignH.CENTER,
        wrap   = true
    )
    private val aBackgroundImg = Image(gdxGame.assetsAll.panel_who)
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    private val aVeldanGroup          = ATmpGroup(screen)
    private val aVeldanDescriptionImg = Image(gdxGame.assetsAll.VELDAN)

    private val aLilyGroup          = ATmpGroup(screen)
    private val aLilyDescriptionImg = Image(gdxGame.assetsAll.LILY)

    private val aSeparatorImg = Image(gdxGame.assetsAll.separator_2)

    private val aSpaceTop    = Actor()
    private val aSpaceBottom = Actor()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBackgroundImg)
        addAndFillActor(aScrollPane)
        setUpVerticalGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun setUpVerticalGroup() {
        aVerticalGroup.width  = width
        aVerticalGroup.height = 1f

        setUpVeldanGroup()
        setUpLilyGroup()
        aSeparatorImg.setSize(1657f, 3f)

        aSpaceTop.setSize(126f, 126f)
        aSpaceBottom.setSize(126f, 126f)

        aVerticalGroup.addActors(
            aSpaceTop,

            aVeldanGroup,
            aSeparatorImg,
            aLilyGroup,

            aSpaceBottom,
        )
    }

    private fun setUpVeldanGroup() {
        aVeldanGroup.setSize(1657f, 1835f)
        aVeldanGroup.addAndFillActor(aVeldanDescriptionImg)

        val aInstagramBtn = Actor()
        val aTelegramBtn  = Actor()
        val aEmailBtn     = Actor()

        aVeldanGroup.addActors(
            aInstagramBtn,
            aTelegramBtn,
            aEmailBtn,
        )

        aInstagramBtn.setBounds(0f, 241f, 1541f, 112f)
        aTelegramBtn .setBounds(0f, 129f, 1656f, 112f)
        aEmailBtn    .setBounds(0f, 17f, 1541f, 112f)

        aInstagramBtn.setOnTouchListener { gdxGame.activity.openInstagram(veldanInstagram) }
        aTelegramBtn.setOnTouchListener { gdxGame.activity.openTelegram(veldanTelegram) }
        aEmailBtn.setOnTouchListener { gdxGame.activity.openEmail(veldanEmail, subject) }
    }

    private fun setUpLilyGroup() {
        aLilyGroup.setSize(1657f, 1611f)
        aLilyGroup.addAndFillActor(aLilyDescriptionImg)

        val aInstagramBtn = Actor()
        val aTelegramBtn  = Actor()
        val aEmailBtn     = Actor()

        aLilyGroup.addActors(
            aInstagramBtn,
            aTelegramBtn,
            aEmailBtn,
        )

        aInstagramBtn.setBounds(0f, 224f, 1541f, 112f)
        aTelegramBtn .setBounds(0f, 112f, 1656f, 112f)
        aEmailBtn    .setBounds(0f, 0f, 1541f, 112f)

        aInstagramBtn.setOnTouchListener { gdxGame.activity.openInstagram(lilyInstagram) }
        aTelegramBtn.setOnTouchListener { gdxGame.activity.openTelegram(lilyTelegram) }
        aEmailBtn.setOnTouchListener { gdxGame.activity.openEmail(lilyEmail, subject) }
    }

}