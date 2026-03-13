package com.lewydo.idlemergecubes.game.actors.panelMenu.settings

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.checkbox.ACheckBox
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import kotlin.String

class ASettingsItem(
    override val screen: AdvancedScreen,
    ls   : Label.LabelStyle,
    type: Type
) : AdvancedGroup() {

    private val currentItemData = listOf(
        ItemData(gdxGame.assetsAll.icons_sound, "Sound"             , ACheckBox.Type.SOUND),
        ItemData(gdxGame.assetsAll.icons_music, "Music"             , ACheckBox.Type.MUSIC),
        ItemData(gdxGame.assetsAll.icons_vibro, "Vibration"         , ACheckBox.Type.VIBRO),
        ItemData(gdxGame.assetsAll.icons_alarm, "Notifications"     , ACheckBox.Type.ALARM),
        ItemData(gdxGame.assetsAll.icons_info , "Who Made This Game", null),
    )[type.ordinal]

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image(gdxGame.assetsAll.settings_item)
    private val aIconImg   = Image(currentItemData.icon)
    private val aTitleLbl  = Label(currentItemData.title, ls)
    private val aBox       = if (currentItemData.boxType == null) null else ACheckBox(screen, currentItemData.boxType)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onCheckBlock: (Boolean) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBgImg)
        addIconImg()
        addTitleLbl()
        if (aBox != null) addBox()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addIconImg() {
        addActor(aIconImg)
        aIconImg.setBounds(48f, 39f, 100f, 100f)
        aIconImg.disable()
    }

    private fun addTitleLbl() {
        addActor(aTitleLbl)
        aTitleLbl.setBounds(172f, 39f, 180f, 98f)
        aTitleLbl.disable()
    }

    private fun addBox() {
        addActor(aBox!!)
        aBox.setBounds(1459f, 31f, 249f, 115f)
        aBox.setOnCheckListener { onCheckBlock(it) }
    }

    data class ItemData(
        val icon    : TextureRegion?  = null,
        val title   : String          = "",
        val boxType : ACheckBox.Type? = null,
    )

    enum class Type {
        SOUND, MUSIC, VIBRO, ALARM, INFO
    }

}