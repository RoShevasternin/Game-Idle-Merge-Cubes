package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lewydo.idlemergecubes.game.manager.util.SoundUtil
import com.lewydo.idlemergecubes.game.utils.TextureEmpty
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActors
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.region

open class AButton(
    override val screen: AdvancedScreen,
    type: Type
) : AdvancedGroup() {

    private val defaultImage  = Image(getStyleByType(type).default)
    private val pressedImage  = Image(getStyleByType(type).pressed).apply { color.a = 0f }
    private val disabledImage = Image(getStyleByType(type).disabled).apply { color.a = 0f }

    private var onClickBlock: () -> Unit = { }

    var touchDownBlock   : AButton.(x: Float, y: Float) -> Unit = { _, _ -> }
    var touchDraggedBlock: AButton.(x: Float, y: Float) -> Unit = { _, _ -> }
    var touchUpBlock     : AButton.(x: Float, y: Float) -> Unit = { _, _ -> }

    private var clickSound: SoundUtil.AdvancedSound? = null
    private var area: Actor? = null

    private val animShowTime = 0.050f
    private val animHideTime = 0.400f


    override fun addActorsOnGroup() {
        addAndFillActors(getActors())
        addListener(getListener())
    }


    private fun getActors() = listOf<Actor>(
        defaultImage,
        pressedImage,
        disabledImage,
    )



    private fun getListener() = object : InputListener() {
        var isWithin     = false
        var isWithinArea = false

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDownBlock(x, y)
            touchDragged(event, x, y, pointer)

            clickSound?.let { gdxGame.soundUtil.play(it) }

            event?.stop()
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            touchDraggedBlock(x, y)

            isWithin = x in 0f..width && y in 0f..height
            area?.let { isWithinArea = x in 0f..it.width && y in 0f..it.height }

            if (isWithin || isWithinArea) press() else unpress()
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            touchUpBlock(x, y)

            if (isWithin || isWithinArea) {
                unpress()
                onClickBlock()
            }
        }
    }

    fun press() {
        defaultImage.clearActions()
        pressedImage.clearActions()

        defaultImage.animHide(animShowTime)
        pressedImage.animShow(animShowTime)
    }

    fun unpress() {
        defaultImage.clearActions()
        pressedImage.clearActions()

        defaultImage.animShow(animHideTime)
        pressedImage.animHide(animHideTime)
    }

    fun disable(useDisabledStyle: Boolean = true) {
        touchable = Touchable.disabled

        if (useDisabledStyle) {
            defaultImage.clearActions()
            pressedImage.clearActions()
            disabledImage.clearActions()

            defaultImage.animHide()
            pressedImage.animHide()
            disabledImage.animShow()
        }

    }

    fun enable() {
        touchable = Touchable.enabled

        defaultImage.clearActions()
        pressedImage.clearActions()
        disabledImage.clearActions()

        defaultImage.animShow()
        pressedImage.animHide()
        disabledImage.animHide()

    }

    fun pressAndDisable(useDisabledStyle: Boolean = false) {
        press()
        disable(useDisabledStyle)
    }

    fun unpressAndEnable() {
        unpress()
        enable()
    }

    fun setStyle(style: AButtonStyle) {
        defaultImage.drawable  = style.default
        pressedImage.drawable  = style.pressed
        disabledImage.drawable = style.disabled
    }

    fun setOnClickListener(sound: SoundUtil.AdvancedSound? = gdxGame.soundUtil.click, block: () -> Unit) {
        clickSound   = sound
        onClickBlock = block
    }

    fun addArea(actor: Actor) {
        area = actor
        actor.addListener(getListener())
    }

    private fun getStyleByType(type: Type) = when(type) {
        Type.NONE -> AButtonStyle(
            default = TextureRegionDrawable(TextureEmpty.region),
            pressed = TextureRegionDrawable(TextureEmpty.region),
            disabled = TextureRegionDrawable(TextureEmpty.region),
        )
        Type.SETTINGS -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.settings_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.settings_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.settings_press),
        )
        Type.BUY -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.buy_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.buy_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.buy_press),
        )
        Type.COLLECT -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.collect_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.collect_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.collect_press),
        )
        Type.COLLECT_X2 -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.collect_x2_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.collect_x2_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.collect_x2_press),
        )

        Type.MENU_ITEM -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.menu_item_section_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.menu_item_section_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.menu_item_section_press),
        )
        Type.MENU_RESET_GAME -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.reset_game_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.reset_game_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.reset_game_press),
        )
        Type.MENU_CLOSE -> AButtonStyle(
            default = TextureRegionDrawable(gdxGame.assetsAll.close_def),
            pressed = TextureRegionDrawable(gdxGame.assetsAll.close_press),
            disabled = TextureRegionDrawable(gdxGame.assetsAll.close_press),
        )
    }

    // ---------------------------------------------------
    // Style
    // ---------------------------------------------------

    data class AButtonStyle(
        var default: Drawable,
        var pressed: Drawable,
        var disabled: Drawable,
    )

    enum class Type {
        NONE, SETTINGS, BUY, COLLECT, COLLECT_X2,
        MENU_ITEM, MENU_RESET_GAME, MENU_CLOSE,


    }

}