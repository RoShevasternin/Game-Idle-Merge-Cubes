package com.lewydo.idlemergecubes.game.actors.panel.grid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabelAutoSize
import com.lewydo.idlemergecubes.game.actors.vfx.AHslImage
import com.lewydo.idlemergecubes.game.utils.CubeColorSystem
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACube(
    override val screen: AdvancedScreen,
    var index : Int,
    var lvl   : Int,
    style     : MsdfStyle,
): AdvancedGroup() {


    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCubeImg      = AHslImage(screen, gdxGame.assetsAll.cube)
    private val aCubeLevelLbl = AMsdfLabelAutoSize(screen, lvl.toString(), style, fitMode = AMsdfLabelAutoSize.FitMode.MIN)

    // ------------------------------------------------------------------------
    // STATE
    // ------------------------------------------------------------------------

    private var currentState = State.DEFAULT

    // ------------------------------------------------------------------------
    // FIELD
    // ------------------------------------------------------------------------

    private var visualColor = Color.WHITE

    var isDragEnabled = true

    // ------------------------------------------------------------------------
    // DRAG CALLBACKS
    // ------------------------------------------------------------------------

    private var onStart: (() -> Unit)? = null
    private var onMove: ((Float, Float) -> Unit)? = null
    private var onEnd: (() -> Unit)? = null

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    // ------------------------------------------------------------------------
    // LIFECYCLE
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        setOrigin(Align.center)

        addCubeImg()
        addCubeLevelLbl()
    }

    // ------------------------------------------------------------------------
    // ADD ACTORS
    // ------------------------------------------------------------------------

    private fun addCubeImg() {
        addAndFillActors(aCubeImg)
        updateColor()
    }

    private fun addCubeLevelLbl() {
        updateLblBounds()
        addActor(aCubeLevelLbl)
        aCubeLevelLbl.label.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // LEVEL / COLOR SYSTEM
    // ------------------------------------------------------------------------

    fun setLevel(newLevel: Int) {
        lvl = newLevel
        aCubeLevelLbl.setText(lvl.toString())
        updateLblBounds()

        updateColor()
        animUpgrade()
    }

    fun updateColor() {
        //aCube1Img.setColorShader(CubeColorSystem.getFrameColor(lvl))
        visualColor = CubeColorSystem.getCubeColor(lvl)

        aCubeImg.setColorShader(visualColor)
    }

    fun getVisualColor(): Color {
        return visualColor
    }

    private fun updateLblBounds() {
        val size = width * if (lvl < 10) 0.5f else 0.75f
        aCubeLevelLbl.setSize(size, size)

        val nx = width / 2f - size / 2f
        val ny = height / 2f - size / 2f
        aCubeLevelLbl.setPosition(nx, ny)
    }

    // ------------------------------------------------------------------------
    // DRAG SYSTEM
    // ------------------------------------------------------------------------

    fun setDragCallbacks(
        onStart: () -> Unit,
        onMove: (Float, Float) -> Unit,
        onEnd: () -> Unit
    ) {
        this.onStart = onStart
        this.onMove  = onMove
        this.onEnd   = onEnd

        addListener(object : DragListener() {

            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int) {
                dragOffsetX = x
                dragOffsetY = y

                onStart.invoke()
            }

            override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (!isDragEnabled) return  // ← не рухаємо якщо заблоковано

                val stageX = event.stageX
                val stageY = event.stageY
                val parentCoords = parent.stageToLocalCoordinates(Vector2(stageX, stageY))
                setPosition(parentCoords.x - dragOffsetX, parentCoords.y - dragOffsetY)
                onMove.invoke(stageX, stageY)
            }

            override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
                isDragEnabled = true  // ← завжди відновлюємо
                onEnd.invoke()
            }
        })
    }

    // ------------------------------------------------------------------------
    // VISUAL STATES
    // ------------------------------------------------------------------------

    // ── Пул: скидання стану для повторного використання (замість new ACube) ──
    fun resetForPool(newIndex: Int, newLvl: Int) {
        index = newIndex
        lvl   = newLvl
        currentState = State.DEFAULT
        clearActions()
        setScale(1f, 1f)
        rotation = 0f
        color.a = 1f
        isDragEnabled = true
        isVisible = true
        aCubeLevelLbl.setText(lvl.toString())
        updateLblBounds()
        updateColor()
    }

    fun setState(state: State) {

        if (state == currentState) return
        currentState = state

        clearActions()

        when (state) {
            State.DEFAULT       -> animDefault()
            State.HOVER_MATCH   -> animHoverMatch()
            State.HOVER_INVALID -> animHoverInvalid()
        }
    }

    // ------------------------------------------------------------------------
    // ANIMATIONS
    // ------------------------------------------------------------------------

    fun animSpawn() {
        setScale(0f)
        addAction(Actions.scaleTo(1f, 1f, 0.18f, Interpolation.swingOut))
    }

    fun animUpgrade() {
        addAction(
            Actions.sequence(
                Actions.scaleTo(1.25f, 1.25f, 0.08f),
                Actions.scaleTo(1f, 1f, 0.12f, Interpolation.swingOut)
            )
        )
    }

    fun animLift() {
        clearActions()

        addAction(
            Actions.parallel(
                Actions.scaleTo(1.12f, 1.12f, 0.12f, Interpolation.sineOut),
                Actions.rotateTo(-6f, 0.12f, Interpolation.sineOut),
                Actions.moveBy(0f, 18f, 0.12f, Interpolation.sineOut)
            )
        )
    }

    private fun animDefault() {
        addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.sineOut))
    }

    private fun animHoverMatch() {
        addAction(Actions.scaleTo(0.9f, 0.9f, 0.1f, Interpolation.sineOut))
    }

    private fun animHoverInvalid() {
        addAction(Actions.scaleTo(0.5f, 0.5f, 0.1f, Interpolation.sineOut))
    }

    // ------------------------------------------------------------------------
    // STATES
    // ------------------------------------------------------------------------

    enum class State {
        DEFAULT,
        HOVER_MATCH,
        HOVER_INVALID
    }

}