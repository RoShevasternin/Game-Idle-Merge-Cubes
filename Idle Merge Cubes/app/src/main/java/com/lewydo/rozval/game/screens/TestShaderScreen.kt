package com.lewydo.rozval.game.screens

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
import com.lewydo.rozval.game.actors.ATmpGroup
import com.lewydo.rozval.game.actors.progress.AProgressDefault
import com.lewydo.rozval.game.actors.shader.ABlurBack
import com.lewydo.rozval.game.actors.shader.AScreenShot
import com.lewydo.rozval.game.actors.shader.ATestShader
import com.lewydo.rozval.game.utils.*
import com.lewydo.rozval.game.utils.actor.addActors
import com.lewydo.rozval.game.utils.actor.animHide
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedStage
import com.lewydo.rozval.game.utils.font.FontParameter
import kotlinx.coroutines.launch

class TestShaderScreen: AdvancedScreen() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
    private val font60    = fontGenerator_LondrinaSolid_Regular.generateFont(parameter.setSize(60))

    private val progress = AProgressDefault(this)
    private val lblFPS   = Label("", LabelStyle(font60, Color.BLACK))

    private var movableActor: AdvancedGroup? = null

    private val tmpGroup = ATmpGroup(this)
    private val scroll   = ScrollPane(tmpGroup)

    override fun show() {
        setBackBackground(gdxGame.assetsAll.listBackgroundLVL.first())
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

        val person1 = Image(gdxGame.assetsLoader.builderList[3])
        person1.debug()
        person1.setBounds(57f, 179f, 200f, 315f)

        val person2 = Image(gdxGame.assetsLoader.builderList[2])
        person2.debug()
        person2.setBounds(0f, 0f, 200f, 315f)

        val test = ATestShader(this@TestShaderScreen)
        test.debug()
        test.setBounds(57f, 566f, 200f, 315f)
        test.addActor(person2)

        test.setOrigin(Align.center)
        test.addAction(Acts.forever(Acts.rotateBy(-360f, 5f)))

        person1.color.a = 0.5f
        person2.color.a = 0.5f

        addActors(person1, test)

        addTest()

        val mainTEST = ABlurBack(this@TestShaderScreen, gdxGame.assetsAll.MASK_CIRCLE)//,game.assetsLoader.builderList[2])
        mainTEST.debug()
        mainTEST.setBounds(500f, 100f, 1000f, 600f)
        addActor(mainTEST)

        val testGroup = ATmpGroup(this@TestShaderScreen)
        testGroup.debug()
        testGroup.setBounds(1063f, 285f, 200f, 315f)
        addActor(testGroup)

        val p1 = Image(gdxGame.assetsLoader.builderList[3])
        p1.debug()
        p1.setBounds(0f, 0f, 200f, 315f)
        testGroup.addActor(p1)

        val p2 = Image(gdxGame.assetsLoader.builderList[2])
        p2.debug()
        p2.setBounds(10f, 5f, 200f, 315f)
        testGroup.addActor(p2)

        val ara = AScreenShot(this@TestShaderScreen)
        ara.debug()
        ara.setBounds(0f, 0f, 500f, 500f)
        addActor(ara)

        coroutine?.launch {
            progress.progressPercentFlow.collect {
                p1.x = it * 3
                p2.x = -it * 3
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
        //tmpGroup.addActor(test)

        val p1 = Image(gdxGame.assetsLoader.builderList[3])
        p1.debug()
        p1.setSize(200f, 315f)

        val p2 = Image(gdxGame.assetsLoader.builderList[2])
        p2.debug()
        p2.setSize(200f, 315f)

        val p3 = Image(gdxGame.assetsLoader.builderList[1])
        p3.debug()
        p3.setSize(200f, 315f)

        test.addActor(p1)
        test2.addActor(p2)
        test.addActor(test2)

        test.setOrigin(Align.center)
        //test.addAction(Acts.forever(Acts.rotateBy(-360f, 5f)))

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

    override fun animHide(blockEnd: Block) {
        stageBack.root.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShow(blockEnd: Block) {}
}