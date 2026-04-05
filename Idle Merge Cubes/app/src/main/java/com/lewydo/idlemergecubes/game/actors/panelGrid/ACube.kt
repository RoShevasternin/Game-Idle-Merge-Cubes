package com.lewydo.idlemergecubes.game.actors.panelGrid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.label.ALabelAutoFont
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.shader.AHslImage
import com.lewydo.idlemergecubes.game.utils.CubeColorSystem
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActors
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACube(
    override val screen: AdvancedScreen,
    var index: Int,
    var lvl  : Int,
    labelStyle: Label.LabelStyle,
): AdvancedGroup() {


    // ------------------------------------------------------------------------
    // VISUAL LAYERS
    // ------------------------------------------------------------------------
    // 1 — Frame
    // 2 — Fill (Tinted)
    // 3 — Highlight / Shine

    //private val aCube3Img = AHslImage(screen, gdxGame.assetsAll.listCube[2])
    //private val aCube1Img = AHslImage(screen, gdxGame.assetsAll.listCube[0])

    private val aCubeImg      = AHslImage(screen, gdxGame.assetsAll.cube)
    private val aCubeLevelLbl = ALabelAutoFont(screen, lvl.toString(), labelStyle, fitMode = ALabelAutoFont.FitMode.MIN)

    // ------------------------------------------------------------------------
    // STATE
    // ------------------------------------------------------------------------

    private var currentState = State.DEFAULT

    // ------------------------------------------------------------------------
    // FIELD
    // ------------------------------------------------------------------------

    private var visualColor = Color.WHITE

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
        addAndFillActors(
            //aCube1Img,
            aCubeImg,
            //aCube3Img
        )
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
                val stageX = event.stageX
                val stageY = event.stageY

                val parentCoords = parent.stageToLocalCoordinates(Vector2(stageX, stageY))

                setPosition(parentCoords.x - dragOffsetX, parentCoords.y - dragOffsetY)

                onMove.invoke(stageX, stageY)
            }

            override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
                onEnd.invoke()
            }
        })
    }

    // ------------------------------------------------------------------------
    // VISUAL STATES
    // ------------------------------------------------------------------------

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