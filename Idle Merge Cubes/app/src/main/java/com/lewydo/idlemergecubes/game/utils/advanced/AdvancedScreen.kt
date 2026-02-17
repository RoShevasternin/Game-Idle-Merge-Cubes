package com.lewydo.idlemergecubes.game.utils.advanced

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.lewydo.idlemergecubes.MainActivity
import com.lewydo.idlemergecubes.game.utils.*
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.font.FontGenerator
import com.lewydo.idlemergecubes.game.utils.font.FontGenerator.Companion.FontPath
import com.lewydo.idlemergecubes.util.cancelCoroutinesAll
import com.lewydo.idlemergecubes.util.currentClassName
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AdvancedScreen(
    val WIDTH : Float = WIDTH_UI,
    val HEIGHT: Float = HEIGHT_UI
) : ScreenAdapter(), IInputAdapter {

    val viewportBack by lazy { ScreenViewport() }
    val stageBack    by lazy { AdvancedStage(viewportBack) }

    val viewportUI by lazy { ExtendViewport(WIDTH, HEIGHT) }
    val stageUI    by lazy { AdvancedStage(viewportUI) }

    val scaleScreenToUiY: Float get() = (viewportUI.worldHeight / Gdx.graphics.height.toFloat())

    val safeTopUI    get() = MainActivity.statusBarHeight * scaleScreenToUiY
    val safeBottomUI get() = MainActivity.navBarHeight * scaleScreenToUiY

    val inputMultiplexer    = InputMultiplexer()

    val backBackgroundImage = Image()
    val uiBackgroundImage   = Image()

    val disposableSet = mutableSetOf<Disposable>()
    var coroutine: CoroutineScope? = CoroutineScope(Dispatchers.Default)
        private set

    val drawerUtil by lazy { ShapeDrawerUtil(stageUI.batch) }

    private val scalerVector = Vector2()
    val scalerUItoScreen     = SizeScaler(SizeScaler.Axis.X, WIDTH_UI)

    val fontGenerator_Nunito_Black     = FontGenerator(FontPath.Nunito_Black)
    val fontGenerator_Nunito_Bold      = FontGenerator(FontPath.Nunito_Bold)
    val fontGenerator_Nunito_ExtraBold = FontGenerator(FontPath.Nunito_ExtraBold)
    val fontGenerator_Nunito_Regular   = FontGenerator(FontPath.Nunito_Regular)
    val fontGenerator_Nunito_SemiBold  = FontGenerator(FontPath.Nunito_SemiBold)

    override fun show() {
        log("show AdvancedScreen: $currentClassName")
        val screenWidth  = Gdx.graphics.width
        val screenHeight = Gdx.graphics.height
        scalerUItoScreen.calculateScale(scalerVector.set(screenWidth.toFloat(), screenHeight.toFloat()))

        stageBack.update(screenWidth, screenHeight, true)
        stageUI.update(screenWidth, screenHeight, true)

        stageBack.root.addAndFillActor(backBackgroundImage)
        stageUI.root.addAndFillActor(uiBackgroundImage)

        stageBack.root.addActorsOnStageBack()
        stageUI.root.addActorsOnStageUI()

        Gdx.input.inputProcessor = inputMultiplexer.apply { addProcessors(this@AdvancedScreen, stageUI, stageBack) }
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
    }

    override fun render(delta: Float) {
        stageBack.render()
        stageUI.render()
        drawerUtil.update()
    }

    override fun dispose() {
        log("dispose AdvancedScreen: $currentClassName")
        disposeAll(
            stageBack, stageUI, drawerUtil,

            fontGenerator_Nunito_Black,
            fontGenerator_Nunito_Bold,
            fontGenerator_Nunito_ExtraBold,
            fontGenerator_Nunito_Regular,
            fontGenerator_Nunito_SemiBold,
        )
        disposableSet.disposeAll()
        inputMultiplexer.clear()
        cancelCoroutinesAll(coroutine)
        coroutine = null
    }

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.BACK -> {
                if (gdxGame.navigationManager.isBackStackEmpty()) gdxGame.navigationManager.exit()
                else animHide { gdxGame.navigationManager.back() }
            }
        }
        return true
    }

    abstract fun animShow(blockEnd: Block = {})
    abstract fun animHide(blockEnd: Block = {})

    open fun Group.addActorsOnStageBack() {}
    open fun Group.addActorsOnStageUI() {}

    fun setBackBackground(region: TextureRegion) {
        backBackgroundImage.drawable = TextureRegionDrawable(region)
    }

    fun setBackBackground(texture: Texture) {
        backBackgroundImage.drawable = TextureRegionDrawable(texture)
    }

    fun setUIBackground(region: TextureRegion) {
        uiBackgroundImage.drawable = TextureRegionDrawable(region)
    }

    fun setUIBackground(texture: Texture) {
        uiBackgroundImage.drawable = TextureRegionDrawable(texture)
    }

    fun setBackgrounds(backRegion: TextureRegion, uiRegion: TextureRegion = backRegion) {
        setBackBackground(backRegion)
        setUIBackground(uiRegion)
    }

    fun setBackgrounds(backTexture: Texture, uiTexture: Texture = backTexture) {
        setBackBackground(backTexture)
        setUIBackground(uiTexture)
    }

}