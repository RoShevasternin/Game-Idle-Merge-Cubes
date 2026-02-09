package com.lewydo.rozval.game.utils.advanced.box2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.lewydo.rozval.game.box2d.WorldUtil
import com.lewydo.rozval.game.utils.HEIGHT_UI
import com.lewydo.rozval.game.utils.HEIGHT_WORLD
import com.lewydo.rozval.game.utils.WIDTH_UI
import com.lewydo.rozval.game.utils.WIDTH_WORLD
import com.lewydo.rozval.game.utils.addProcessors
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedStage
import com.lewydo.rozval.game.utils.camera.CameraGestureListener
import com.lewydo.rozval.game.utils.camera.cameraController
import com.lewydo.rozval.util.currentClassName
import com.lewydo.rozval.util.log

abstract class AdvancedBox2dScreen(
    val worldUtil: WorldUtil,
    val uiW  : Float = WIDTH_UI,
    val uiH  : Float = HEIGHT_UI,
    val worldW : Float = WIDTH_WORLD,
    val worldH : Float = HEIGHT_WORLD,
): AdvancedScreen(uiW, uiH) {

    val viewportDebug by lazy { ExtendViewport(worldW, worldH) }

    val viewportWorld by lazy { ExtendViewport(WIDTH, HEIGHT) }
    val stageWorld    by lazy { AdvancedStage(viewportWorld) }

    protected val cameraGestureListener by lazy {
        CameraGestureListener(
            viewportWorld.camera as OrthographicCamera,
            viewportDebug.camera as OrthographicCamera,
        )
    }
    protected val gestureDetector by lazy { GestureDetector(cameraGestureListener) }

    override fun show() {
        super.show()

        val screenWidth  = Gdx.graphics.width
        val screenHeight = Gdx.graphics.height

        stageWorld.update(screenWidth, screenHeight, true)
        viewportDebug.update(screenWidth, screenHeight, true)

        stageWorld.root.addActorsOnStageWorld()

        inputMultiplexer.clear()
        inputMultiplexer.addProcessors(
            this,              // Твої клавіші (BACK і т.д.)
            stageUI,           // Кнопки інтерфейсу мають найвищий пріоритет
            gestureDetector,    // Камера рухається, якщо ми НЕ натиснули на кнопку в UI
            stageWorld,        // Об'єкти в ігровому світі (наприклад, блоки)
            stageBack          // Фон
        )

        cameraController.add(viewportWorld.camera as OrthographicCamera)
    }

    override fun render(delta: Float) {
        worldUtil.update(delta)
        cameraController.update(delta)

        stageBack.render()
        stageWorld.render()
        stageUI.render()

        drawerUtil.update()

        if (WorldUtil.isDebug) {
            viewportDebug.apply()
            worldUtil.debug(viewportDebug.camera.combined)
        }
    }

    override fun dispose() {
        log("dispose AdvancedBox2dScreen: $currentClassName")//${this::class.java.name.substringAfterLast('.')}")
        worldUtil.dispose()
        stageWorld.dispose()
        super.dispose()
    }

    abstract fun Group.addActorsOnStageWorld()

}