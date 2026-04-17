package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.progress.AProgressDefault
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.setOnClickListener
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.actors.vfx.ABlur
import kotlinx.coroutines.launch

class TestShaderScreen: AdvancedScreen() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font60    = fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(260))

    private val progress = AProgressDefault(this)
    private val lblFPS   = Label("", LabelStyle(font60, Color.BLACK))

    private var movableActor: AdvancedGroup? = null

    private val tmpGroup = ATmpGroup(this)
    private val scroll   = ScrollPane(tmpGroup)

    private val aRedImg   = Image(gdxGame.assetsAll.red)
    private val aGreenImg = Image(gdxGame.assetsAll.green)

    override fun show() {
        setBackBackground(gdxGame.assetsAll.bg_test)
        //setBackBackground(drawerUtil.getRegion(Color.GRAY))
        //setUIBackground(game.assetsAll.LVL_1.region)
        super.show()

        stageUI.root.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                super.touchDragged(event, x, y, pointer)
                val halfW = movableActor?.let { it.width / 2f } ?: 0f
                val halfH = movableActor?.let { it.height / 2f } ?: 0f
                movableActor?.setPosition(x - halfW, y - halfH)
            }
        })
    }

    override fun Group.addActorsOnStageUI() {
        addActor(progress)
        progress.setBounds(208f, 3500f, 1743f, 412f)

        addActor(lblFPS)
        lblFPS.apply {
            setBounds(642f, 0f, 876f, 360f)
            setAlignment(Align.center)

            var isTestVisible = true
            this.setOnClickListener {
                if (isTestVisible) {
                    animHideScreen()
                } else {
                    animShowScreen()
                }
                isTestVisible = !isTestVisible
            }
        }

//        val test = ATestShader(this@TestShaderScreen)
//        test.debug()
//        test.setBounds(57f, 566f, 200f, 315f)
//
//        test.setOrigin(Align.center)
//        test.addAction(Actions.forever(Actions.rotateBy(-360f, 5f)))
//
//        addTest()

        val mainTEST = ABlur(this@TestShaderScreen) //,game.assetsLoader.builderList[2])
        mainTEST.debug()

        mainTEST.setBounds(374f, 725f, 1413f, 1413f)
        addActor(mainTEST)
        movableActor = mainTEST

        val imgCoin = Image(gdxGame.assetsAll.COIN_BIG)
        mainTEST.addAndFillActor(imgCoin)


//        var nx = 374f
//        repeat(50) {
//            val mainTEST2 = ABlurTest(this@TestShaderScreen) //ABlurBack(this@TestShaderScreen)//,game.assetsLoader.builderList[2])
//            mainTEST2.debug()
//
//            mainTEST2.radiusBlur = 5f
//
//            mainTEST2.setBounds(nx, 725f, 1413f, 1413f)
//            addActor(mainTEST2)
//
//            val imgCoin2 = Image(gdxGame.assetsAll.COIN_BIG)
//            mainTEST2.addAndFillActor(imgCoin2)
//
//            nx += 5f
//        }


        addActorWithConstraints(aRedImg) {
            startToStartOf = mainTEST
            bottomToTopOf  = mainTEST
            marginStart    = 177f
            marginBottom   = 95f
        }
        addActorWithConstraints(aGreenImg) {
            endToEndOf     = mainTEST
            bottomToTopOf  = mainTEST
            marginEnd      = 177f
            marginBottom   = 95f
        }

        aGreenImg.toBack()
        aRedImg.toBack()

        coroutine?.launch {
            progress.progressPercentFlow.collect {
                mainTEST.radiusBlur = it

                if (it > 60) {
                    mainTEST.isStaticEffect = true
                } else {
                    mainTEST.isStaticEffect = false
                }
            }
        }

    }

    override fun render(delta: Float) {
        super.render(delta)
        lblFPS.setText("FPS: " + Gdx.graphics.framesPerSecond)
    }

    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }
}