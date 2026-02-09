package com.lewydo.rozval.game.actors.button.location.button

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.rozval.game.actors.button.location.actors.AStarsNecessary
import com.lewydo.rozval.game.actors.button.location.separator.AAvailableSeparatorButton
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.util.log

class ALockStarLevelButton(
    override val screen    : AdvancedScreen,
    override val level     : Int,
    override val labelStyle: Label.LabelStyle,
    necessaryStars: Int,
) : AbstractLevelButton() {

    private val assetsAll = gdxGame.assetsAll

    // Actors
    private val panelImg       = Image(assetsAll.lvl_btn_dis)
    private val lockImg        = Image(assetsAll.lock_def)
    private val starsNecessary = AStarsNecessary(screen, necessaryStars, labelStyle)

    override fun addActorsOnGroup() {
        setSaturationPreview(0f)

        addAndFillActor(panelImg)
        super.addActorsOnGroup()

        addLockBtn()
        addStarNecessary()

    }

    // Actors ------------------------------------------------------------------------

    private fun addLockBtn() {
        addActor(lockImg)
        lockImg.setBounds(55f, 105f, 140f, 140f)
    }

    private fun addStarNecessary() {
        addActor(starsNecessary)
        starsNecessary.setBounds(86f, 17f, 78f, 30f)
    }

    // Logic ------------------------------------------------------------------------

    fun changeToAvailableLevelButton() {
        val availableLevelButton = AAvailableLevelButton(screen, level, labelStyle)

        animHide(0.25f) {
            disposeAndClearChildren()
            addAndFillActor(availableLevelButton)
            animShow(0.25f)
        }
    }

}