package com.lewydo.rozval.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.actors.mainPanel.GameMainPanel
import com.lewydo.rozval.game.actors.progress.AProgressDefault
import com.lewydo.rozval.game.actors.shader.test.ABlock
import com.lewydo.rozval.game.box2d.BodyId
import com.lewydo.rozval.game.box2d.bodies.BBrick
import com.lewydo.rozval.game.box2d.bodies.BPersik
import com.lewydo.rozval.game.box2d.bodies.BWood
import com.lewydo.rozval.game.box2d.bodiesGroup.BGBorders
import com.lewydo.rozval.game.utils.*
import com.lewydo.rozval.game.utils.actor.HAlign
import com.lewydo.rozval.game.utils.actor.VAlign
import com.lewydo.rozval.game.utils.actor.addActorAligned
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.actor.animDelay
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.actor.animShow
import com.lewydo.rozval.game.utils.actor.setBounds
import com.lewydo.rozval.game.utils.actor.setOnClickListener
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.box2d.AdvancedBox2dUserScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedStage
import com.lewydo.rozval.game.utils.font.FontParameter
import com.lewydo.rozval.util.log
import kotlinx.coroutines.launch

class GameScreen(): AdvancedBox2dUserScreen() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font30    = fontGenerator_LondrinaSolid_Regular.generateFont(parameter.setSize(30))

    private val progress = AProgressDefault(this)
    private val lblFPS   = Label("", LabelStyle(font30, GameColor.white))
    private val reset    = Image(gdxGame.assetsAll.RESET)

    private val mainPanel = GameMainPanel(this)

    private val bPersic = BPersik(this)
    private val bBrick  = BBrick(this)
    private val bBlock  = BWood(this)

    private val bgBorders = BGBorders(this)

    override fun show() {
        stageBack.root.color.a = 0f
        stageUI.root.color.a   = 0f

        setBackBackground(gdxGame.assetsAll.listBackgroundLVL[MenuScreen.LVL_CLICK-1])
        //setUIBackground(drawerUtil.getTexture(GameColor.background))

        super.show()

        animShow {
            log("Hello World")
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        lblFPS.setText("FPS: " + Gdx.graphics.framesPerSecond)
    }

    override fun animShow(blockEnd: Block) {
        stageUI.root.children.onEach { it.clearActions() }

        stageBack.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animShow(TIME_ANIM_SCREEN)

        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animHide(blockEnd: Block) {
        stageUI.root.children.onEach { it.clearActions() }

        stageBack.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animHide(TIME_ANIM_SCREEN)

        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun Group.addActorsOnStageUI() {
        addTestFPSandProgress()
        addBtnReset()

        addMainPanel()
    }

    override fun Group.addActorsOnStageWorld() {
        //addAndFillActor(Image(drawerUtil.getTexture(GameColor.background)))
        addActor(Image(gdxGame.assetsAll.GRID).also { it.setSize(WIDTH, HEIGHT) })

        create_BGBorders()
        create_BPersik()
        create_BBrick()
        create_BBlock()
    }

    // Actors ------------------------------------------------------------------------

    private fun Group.addMainPanel() {
        mainPanel.setSize(155f, 1080f)
        addActorAligned(mainPanel, HAlign.END, VAlign.CENTER)

        mainPanel.menuBtnBlock = {
            animHide { gdxGame.navigationManager.back() }
        }
    }

    private fun Group.addTestFPSandProgress() {
        addActor(progress)
        progress.setBounds(159f, 944f, 698f, 106f)

        addActor(Image(drawerUtil.getTexture(GameColor.background)).apply { setBounds(857f, 973f, 164f, 46f) })
        addActor(lblFPS)
        lblFPS.apply {
            setBounds(857f, 973f, 164f, 46f)
            setAlignment(Align.center)
        }

        coroutine?.launch {
            progress.progressPercentFlow.collect {
                (bBlock.actor as ABlock).updateDamage(it)
            }
        }
    }

    private fun Group.addBtnReset() {
        addActor(reset)
        reset.setBounds(39f, 944f, 106f, 106f)

        reset.setOnClickListener(gdxGame.soundUtil) {
            cameraGestureListener.reset()
        }
    }

    // Bodies ------------------------------------------------------------------------

    private fun create_BPersik() {
        bPersic.id = BodyId.PERSIK
        bPersic.create(1265f, 128f, 200f, 315f)
    }

    private fun create_BBrick() {
        bBrick.id = BodyId.ITEM
        bBrick.create(97f, 363f, 134f, 134f)
        bBrick.body?.gravityScale = 0f

        val startPosX = bBrick.actor!!.x

        bBrick.renderBlockArray.add {
            //log("x = ${bBrick.actor?.x} | $startPosX")
            if (bBrick.actor!!.x !in ((startPosX-3f)..(startPosX+3f))) {
                bBrick.renderBlockArray.clear()
                bBrick.body?.gravityScale = 1f
            }
        }
    }

    private fun create_BBlock() {
        bBlock.id = BodyId.BLOCK
        bBlock.create(1541f, 172f, 156f, 156f)
    }

    // Bodies Groups ------------------------------------------------------------------------

    private fun create_BGBorders() {
        bgBorders.create(-41f, 62f, 1842f, 1073f)
    }


}