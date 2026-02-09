package com.lewydo.rozval.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.ATmpGroup
import com.lewydo.rozval.game.actors.autoLayout.ATableGroup
import com.lewydo.rozval.game.actors.autoLayout.AutoLayout
import com.lewydo.rozval.game.actors.button.location.button.AAvailableLevelButton
import com.lewydo.rozval.game.actors.button.location.button.ALockStarKeyLevelButton
import com.lewydo.rozval.game.actors.button.location.button.ALockStarLevelButton
import com.lewydo.rozval.game.actors.button.location.button.AbstractLevelButton
import com.lewydo.rozval.game.actors.button.location.separator.ALockSeparatorButton
import com.lewydo.rozval.game.actors.button.location.separator.AbstractSeparatorButton
import com.lewydo.rozval.game.actors.mainPanel.MenuMainPanel
import com.lewydo.rozval.game.utils.Acts
import com.lewydo.rozval.game.utils.Block
import com.lewydo.rozval.game.utils.TIME_ANIM_SCREEN
import com.lewydo.rozval.game.utils.WIDTH_UI
import com.lewydo.rozval.game.utils.actor.HAlign
import com.lewydo.rozval.game.utils.actor.VAlign
import com.lewydo.rozval.game.utils.actor.addActorAligned
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.animDelay
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.actor.setBounds
import com.lewydo.rozval.game.utils.actor.setPosition
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedStage
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.game.utils.manager.LevelButtonManager
import com.lewydo.rozval.game.utils.manager.LocationManager
import com.lewydo.rozval.util.log
import kotlinx.coroutines.launch
import kotlin.collections.get
import kotlin.invoke

class ThanksScreen: AdvancedScreen() {

    private val groupLewydo    = ATmpGroup(this)
    private val imgLewydoTM    = Image(gdxGame.assetsAll.Lewydo_TM)
    private val imgLewydoHeart = Image(gdxGame.assetsAll.lewydo_heart)
    private val imgBuiltWith   = Image(gdxGame.assetsAll.built_with)
    private val imgLibGDX      = Image(gdxGame.assetsAll.libGDX)

    override fun show() {
        super.show()
        animShow {
            animHide { gdxGame.navigationManager.navigate(MenuScreen::class.java.name) }
        }
    }

    override fun Group.addActorsOnStageUI() {
        addLewydo()
        addImgBuiltWith()
        addImgLibGDX()
    }

    override fun animHide(blockEnd: Block) {
        //stageUI.root.children.onEach { it.clearActions() }

        gdxGame.soundUtil.apply { play(hide) }
        stageUI.root.addAction(Acts.sequence(
            Acts.delay(0.25f),
            Acts.fadeOut(TIME_ANIM_SCREEN),
            Acts.run { blockEnd() }
        ))
    }

    override fun animShow(blockEnd: Block) {
        //stageUI.root.children.onEach { it.clearActions() }

        //stageUI.root.animShow(TIME_ANIM_SCREEN)
        //stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }

        stageUI.root.addAction(Acts.sequence(
            animShowLewydo(),
            Acts.delay(1.35f),
            animLewydoHeart(),
            animShowBuiltWith(),
            Acts.delay(1.3f),
            animMoveBuiltWithDown(),
            animShowLibGDX(),
            Acts.delay(2.5f),

            Acts.run {
                log("Animation Finished")
                blockEnd()
            }
        ))
    }

    // Add Actors ------------------------------------------------------------------------

    private fun Group.addLewydo() {
        groupLewydo.setSize(380f, 63f)
        addActorAligned(groupLewydo, HAlign.CENTER, VAlign.CENTER)

        groupLewydo.y = -70f

        groupLewydo.apply {
            addAndFillActor(imgLewydoTM)
            addActor(imgLewydoHeart)
        }

        imgLewydoHeart.apply {
            setBounds(235f, 7f, 50f, 50f)
            setOrigin(Align.center)
        }

    }

    private fun Group.addImgBuiltWith() {
        imgBuiltWith.setSize(433f, 64f)
        addActorAligned(imgBuiltWith, HAlign.CENTER, VAlign.CENTER)

        imgBuiltWith.color.a = 0f
        imgBuiltWith.y -= 30f

        imgBuiltWith.setOrigin(Align.center)
    }

    private fun Group.addImgLibGDX() {
        imgLibGDX.setSize(433f, 232f)
        addActorAligned(imgLibGDX, HAlign.CENTER, VAlign.CENTER)

        imgLibGDX.setOrigin(Align.center)
        imgLibGDX.setScale(0f, 0f)
    }

    // Anim ------------------------------------------------------------------------

    private fun animShowLewydo(): Action {
        return Acts.run {
            // Додаємо екшн прямо актору
            groupLewydo.addAction(Acts.moveTo(groupLewydo.x, 27f, 1.35f, Interpolation.swingOut))

            gdxGame.soundUtil.apply { play(show_lewydo_tm) }

        }
    }

    private fun animLewydoHeart(): Action {
        return Acts.run {
            imgLewydoHeart.addAction(Acts.forever(
                Acts.sequence(
                    // Перший удар (швидкий)
                    Acts.run { gdxGame.soundUtil.apply { play(heart) } },
                    Acts.scaleTo(1.25f, 1.25f, 0.1f, Interpolation.pow2Out),
                    Acts.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.pow2In),

                    // Другий удар (трішки слабший і швидший)
                    Acts.run { gdxGame.soundUtil.apply { play(heart, 0.5f) } },
                    Acts.scaleTo(1.15f, 1.15f, 0.1f, Interpolation.pow2Out),
                    Acts.scaleTo(1.0f, 1.0f, 0.15f, Interpolation.pow2In),

                    // Пауза між ударами серця
                    Acts.delay(0.6f)
                )
            ))
        }
    }

    private fun animShowBuiltWith(): Action {
        return Acts.run {
            // Додаємо екшн прямо актору
            imgBuiltWith.addAction(Acts.parallel(
                Acts.fadeIn(0.6f),
                Acts.moveBy(0f, 30f, 0.8f, Interpolation.pow2Out)
            ))
        }
    }

    private fun animShowLibGDX(): Action {
        return Acts.run {
            imgLibGDX.addAction(Acts.sequence(
                Acts.run { gdxGame.soundUtil.apply { play(show_libgdx) } },
                Acts.scaleTo(1f, 1f, 0.6f, Interpolation.swingOut),
                Acts.delay(0.5f),
                Acts.run { gdxGame.soundUtil.apply { play(LibGDX) } },
            ))
        }
    }

    private fun animMoveBuiltWithDown(): Action {
        return Acts.run {
            imgBuiltWith.addAction(Acts.parallel(
                Acts.moveBy(0f, -184f, 0.5f, Interpolation.swingOut),
                Acts.scaleTo(0.5f, 0.5f, 0.5f, Interpolation.pow2Out),
            ))
        }
    }

}