package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.progress.AProgressDefault
import com.lewydo.idlemergecubes.game.actors.shader.ABlurBack
import com.lewydo.idlemergecubes.game.actors.shader.AScreenShot
import com.lewydo.idlemergecubes.game.actors.shader.ATestShader
import com.lewydo.idlemergecubes.game.utils.*
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedStage
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import kotlinx.coroutines.launch

class TestShaderScreen: AdvancedScreen() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font60    = fontGenerator_Nunito_SemiBold.generateFont(parameter.setSize(60))

    private val progress = AProgressDefault(this)
    private val lblFPS   = Label("", LabelStyle(font60, Color.BLACK))

    private var movableActor: AdvancedGroup? = null

    private val tmpGroup = ATmpGroup(this)
    private val scroll   = ScrollPane(tmpGroup)

    override fun show() {
        setBackBackground(gdxGame.assetsAll.panel_lvl)
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
                //movableActor?.setPosition(x, y)
            }
        })
    }

    override fun Group.addActorsOnStageUI() {
        addActor(progress)
        progress.setBounds(420f, 946f, 1080f, 80f)

        addActor(lblFPS)
        lblFPS.apply {
            setBounds(29f, 960f, 278f, 79f)
            setAlignment(Align.center)
        }

        val test = ATestShader(this@TestShaderScreen)
        test.debug()
        test.setBounds(57f, 566f, 200f, 315f)

        test.setOrigin(Align.center)
        test.addAction(Actions.forever(Actions.rotateBy(-360f, 5f)))

        addTest()

        val mainTEST = ABlurBack(this@TestShaderScreen)//,game.assetsLoader.builderList[2])
        mainTEST.debug()
        mainTEST.setBounds(500f, 100f, 1000f, 600f)
        addActor(mainTEST)

        val testGroup = ATmpGroup(this@TestShaderScreen)
        testGroup.debug()
        testGroup.setBounds(1063f, 285f, 200f, 315f)
        addActor(testGroup)

        val ara = AScreenShot(this@TestShaderScreen)
        ara.debug()
        ara.setBounds(0f, 0f, 500f, 500f)
        addActor(ara)

        coroutine?.launch {
            progress.progressPercentFlow.collect {
                mainTEST.radiusBlur = it
                mainTEST.x = it * 10
                ara.x = it * 10

                if (it > 60) {
                    mainTEST.isStaticEffect = true
                } else {
                    mainTEST.isStaticEffect = false
                }
            }
        }

        addScroll()
    }

    private fun Group.addTest() {
        val test = ATestShader(this@TestShaderScreen)
        test.debug()
        test.setBounds(400f, 50f, 200f, 315f)
        //movableActor = test

        val test2 = ATestShader(this@TestShaderScreen)
        test2.debug()
        test2.setBounds(10f, 5f, 200f, 315f)

        addActor(test)
        test.addActor(test2)

        test.setOrigin(Align.center)
        //test.addAction(Actions.forever(Actions.rotateBy(-360f, 5f)))

        test.color.a = 0.5f
        test2.color.a = 0.5f

        coroutine?.launch {
            progress.progressPercentFlow.collect {
                //p1.x = it * 3
                //p2.x = -it * 3

                //test.x = it * 3
            }
        }
    }

    private fun Group.addScroll() {
        addActor(scroll)
        scroll.setBounds(1263f, 109f, 602f, 707f)

        tmpGroup.setSize(1000f, 2000f)

        scroll.debug()
        tmpGroup.debug()
    }

    override fun render(delta: Float) {
        super.render(delta)
        lblFPS.setText("FPS: " + Gdx.graphics.framesPerSecond)
    }

    override fun animHideScreen(blockEnd: Block) {
        stageBack.root.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {}
}