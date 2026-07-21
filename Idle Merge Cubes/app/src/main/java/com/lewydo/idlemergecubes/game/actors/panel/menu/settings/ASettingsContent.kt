package com.lewydo.idlemergecubes.game.actors.panel.menu.settings

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.BuildConfig
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.layout.autoLayout.AAutoLayout
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ASettingsContent(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleItem = MsdfStyle(msdf, msdf.fontNunitoSemiBold, 72f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aSeparatorImg = Image(gdxGame.assetsAll.settings_separator)

    private val aVerticalGroup = AAutoLayout(screen, direction = AAutoLayout.Direction.VERTICAL, gapMain = 24f)
    private val aSettItemSound = ASettingsItem(screen, styleItem, ASettingsItem.Type.SOUND)
    private val aSettItemMusic = ASettingsItem(screen, styleItem, ASettingsItem.Type.MUSIC)
    private val aSettItemVibro = ASettingsItem(screen, styleItem, ASettingsItem.Type.VIBRO)
    private val aSettItemAlarm = ASettingsItem(screen, styleItem, ASettingsItem.Type.ALARM)
    private val aSettItemInfo  = ASettingsItem(screen, styleItem, ASettingsItem.Type.INFO)

    private val aVersionLbl = AMsdfLabel("Game Version: ${BuildConfig.VERSION_NAME}", styleItem, color = GameColor.white_55)

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
            aVerticalGroup.add(item)

            if (index == 4) return@forEachIndexed // для aSettItemInfo немає лямбди з listBlock
            item.onCheckBlock = { listBlock[index].get().invoke(it) }
        }

        aSettItemInfo.setOnClickListener { onInfoBlock.invoke() }
    }

    private fun addVersionLbl() {
        addActor(aVersionLbl)
        aVersionLbl.setBounds(554f, 0f, 657f, 98f)
        aVersionLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setInitStateBox(
        sound: Boolean,
        music: Boolean,
        vibro: Boolean,
        alarm: Boolean,
    ) {
        aSettItemSound.initState(sound)
        aSettItemMusic.initState(music)
        aSettItemVibro.initState(vibro)
        aSettItemAlarm.initState(alarm)
    }

}