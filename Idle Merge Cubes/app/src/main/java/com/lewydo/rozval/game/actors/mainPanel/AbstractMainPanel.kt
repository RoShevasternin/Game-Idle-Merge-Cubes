package com.lewydo.rozval.game.actors.mainPanel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.ATmpGroup
import com.lewydo.rozval.game.actors.checkbox.ACheckBox
import com.lewydo.rozval.game.actors.settings.ASetting
import com.lewydo.rozval.game.utils.Block
import com.lewydo.rozval.game.utils.GameColor
import com.lewydo.rozval.game.utils.TIME_ANIM_SCREEN
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.addAndFillActors
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.actor.disable
import com.lewydo.rozval.game.utils.actor.enable
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.gdxGame

abstract class AbstractMainPanel(final override val screen: AdvancedScreen): AdvancedGroup() {

    open val isBackground = false

    private val parentGroup = ATmpGroup(screen)

    private val backgroundImg = Image(screen.drawerUtil.getRegion(GameColor.background.cpy().apply { a = 0.65f }))
    private val panelImg      = Image(gdxGame.assetsAll.PANEL)
    private val musicCBox     = ACheckBox(screen, ACheckBox.Type.MUSIC)
    private val soundCBox     = ACheckBox(screen, ACheckBox.Type.SOUND)
    private val musicSettings = ASetting(screen, ASetting.Static.Type.Music).apply { color.a = 0f }
    private val soundSettings = ASetting(screen, ASetting.Static.Type.Sound).apply { color.a = 0f }

    final override fun addActorsOnGroup() {
        if (isBackground) addAndFillActor(backgroundImg)
        addAndFillActors(panelImg, parentGroup)
        parentGroup.addActorsOnParentGroup()

        addMusic()
        addSound()
        addMusicSetting()
        addSoundSetting()
    }

    abstract fun AdvancedGroup.addActorsOnParentGroup()

    // Actors ------------------------------------------------------------------------

    private fun addMusic() {
        addActor(musicCBox)
        musicCBox.apply {
            setBounds(46f, 286f, 67f, 67f)
            setOrigin(Align.center)
            setOnCheckListener { isCheck ->
                if (isCheck) {
                    listOf(soundCBox, parentGroup).onEach { it.animHide(TIME_ANIM_SCREEN) }

                    musicCBox.animToSettings {
                        musicSettings.animShow(TIME_ANIM_SCREEN) {
                            musicSettings.enable()
                        }
                    }
                }
            }
        }
    }

    private fun addSound() {
        addActor(soundCBox)
        soundCBox.apply {
            setBounds(46f, 172f, 67f, 67f)
            setOrigin(Align.center)
            setOnCheckListener { isCheck ->
                if (isCheck) {
                    listOf(musicCBox, parentGroup).onEach { it.animHide(TIME_ANIM_SCREEN) }

                    soundCBox.animToSettings {
                        soundSettings.animShow(TIME_ANIM_SCREEN) {
                            soundSettings.enable()
                        }
                    }
                }
            }
        }
    }

    private fun addMusicSetting() {
        musicSettings.disable()
        addAndFillActor(musicSettings)

        musicSettings.applyBlock = {
            musicSettings.disable()
            musicSettings.animHide(TIME_ANIM_SCREEN) {
                musicCBox.animFromSettings(286f) {
                    musicCBox.uncheck(false)
                    listOf(soundCBox, parentGroup).onEach { it.animShow(TIME_ANIM_SCREEN) }
                }
            }
        }
    }

    private fun addSoundSetting() {
        soundSettings.disable()
        addAndFillActor(soundSettings)

        soundSettings.applyBlock = {
            soundSettings.disable()
            soundSettings.animHide(TIME_ANIM_SCREEN) {
                soundCBox.animFromSettings(172f) {
                    soundCBox.uncheck(false)
                    listOf(musicCBox, parentGroup).onEach { it.animShow(TIME_ANIM_SCREEN) }
                }
            }
        }
    }

    // Anim ------------------------------------------------------------------------

    private fun AdvancedGroup.animToSettings(block: Block) {
        disable()
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(46f, 186f, 0.3f),
                Actions.scaleBy(0.13f, 0.13f, 0.3f)
            ),
            Actions.run { block.invoke() }
        ))
    }

    private fun Actor.animFromSettings(ny: Float, block: Block) {
        enable()
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(46f, ny, 0.3f),
                Actions.scaleBy(-0.13f, -0.13f)
            ),
            Actions.run { block.invoke() }
        ))
    }

}