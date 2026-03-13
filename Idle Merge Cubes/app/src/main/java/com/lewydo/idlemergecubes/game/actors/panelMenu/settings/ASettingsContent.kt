package com.lewydo.idlemergecubes.game.actors.panelMenu.settings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.actors.layout.linear.AVerticalGroup
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ASettingsContent(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font72 = screen.fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(72))

    private val lsItem = Label.LabelStyle(font72, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aSeparatorImg = Image(gdxGame.assetsAll.settings_separator)

    private val aVerticalGroup = AVerticalGroup(screen, 24f)
    private val aSettItemSound = ASettingsItem(screen, lsItem, ASettingsItem.Type.SOUND)
    private val aSettItemMusic = ASettingsItem(screen, lsItem, ASettingsItem.Type.MUSIC)
    private val aSettItemVibro = ASettingsItem(screen, lsItem, ASettingsItem.Type.VIBRO)
    private val aSettItemAlarm = ASettingsItem(screen, lsItem, ASettingsItem.Type.ALARM)
    private val aSettItemInfo  = ASettingsItem(screen, lsItem, ASettingsItem.Type.INFO)

    private val aVersionLbl = Label("Game Version: ${BuildConfig.VERSION_NAME}", Label.LabelStyle(font72, GameColor.white_55))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onSoundBlock: (Boolean) -> Unit = {}
    var onMusicBlock: (Boolean) -> Unit = {}
    var onVibroBlock: (Boolean) -> Unit = {}
    var onAlarmBlock: (Boolean) -> Unit = {}
    var onInfoBlock : () -> Unit = {}

    private val listBlock = listOf(
        ::onSoundBlock,
        ::onMusicBlock,
        ::onVibroBlock,
        ::onAlarmBlock,
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addSeparatorImg()
        addItems()
        addVersionLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addSeparatorImg() {
        addActor(aSeparatorImg)
        aSeparatorImg.setBounds(0f, 1209f, 1764f, 9f)
    }

    private fun addItems() {
        addActor(aVerticalGroup)
        aVerticalGroup.setBounds(4f, 194f, 1756f, 972f)

        listOf(
            aSettItemSound,
            aSettItemMusic,
            aSettItemVibro,
            aSettItemAlarm,
            aSettItemInfo,
        ).forEachIndexed { index, item ->
            item.setSize(1756f, 178f)
            aVerticalGroup.addActor(item)

            if (index == 4) return // для aSettItemInfo немає лямбди з listBlock
            item.onCheckBlock = listBlock[index].get()
        }

        aSettItemInfo.setOnClickListener { onInfoBlock.invoke() }
    }

    private fun addVersionLbl() {
        addActor(aVersionLbl)
        aVersionLbl.setBounds(554f, 0f, 657f, 98f)
        aVersionLbl.setAlignment(Align.center)
    }

}