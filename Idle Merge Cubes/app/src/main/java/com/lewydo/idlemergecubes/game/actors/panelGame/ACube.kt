package com.lewydo.idlemergecubes.game.actors.panelGame

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.Acts
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.util.log

class ACube(
    override val screen: AdvancedScreen,
    var index: Int,
    var lvl  : Int,
    labelStyle: Label.LabelStyle,
): AdvancedGroup() {

    private val aCubeImg      = Image(gdxGame.assetsAll.cube)
    private val aCubeLevelLbl = Label(lvl.toString(), labelStyle)

    // Field

    private var onStart: (() -> Unit)? = null
    private var onMove: ((Float, Float) -> Unit)? = null
    private var onEnd: (() -> Unit)? = null

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    override fun addActorsOnGroup() {
        addCubeImg()
        addCubeLevelLbl()

        setOrigin(Align.center)
    }

    // Actors ------------------------------------------------------------------------

    private fun addCubeImg() {
        addAndFillActor(aCubeImg)
    }

    private fun addCubeLevelLbl() {
        addAndFillActor(aCubeLevelLbl)
        aCubeLevelLbl.setAlignment(Align.center)
    }

    // Logic ------------------------------------------------------------------------

    fun setLevel(newLevel: Int) {
        lvl = newLevel
        //aCubeImg.drawable = gdxGame.assetsAll.getCubeTexture(level)
        aCubeLevelLbl.setText(lvl.toString())

        animUpgrade()
    }

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

                log("dragStart | $x $y")
                onStart.invoke()
            }

            override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
                val stageX = event.stageX
                val stageY = event.stageY

                val parentCoords = parent.stageToLocalCoordinates(Vector2(stageX, stageY))

                log("drag | $x $y | stage = $stageX $stageY | parent = $parentCoords")

                setPosition(parentCoords.x - dragOffsetX, parentCoords.y - dragOffsetY)

                onMove.invoke(stageX, stageY)
            }

            override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
                onEnd.invoke()
            }
        })
    }

    // Anim ------------------------------------------------------------------------

    fun animSpawn() {
        setScale(0f)
        addAction(
            Acts.scaleTo(1f, 1f, 0.18f, Interpolation.swingOut)
        )
    }

    fun animUpgrade() {
        addAction(
            Acts.sequence(
                Acts.scaleTo(1.2f, 1.2f, 0.1f),
                Acts.scaleTo(1f, 1f, 0.1f)
            )
        )
    }

    fun animLift() {
        clearActions()

        addAction(
            Acts.parallel(
                Acts.scaleTo(1.12f, 1.12f, 0.12f, Interpolation.sineOut),
                Acts.rotateTo(-6f, 0.12f, Interpolation.sineOut),
                Acts.moveBy(0f, 18f, 0.12f, Interpolation.sineOut)
            )
        )
    }

    fun animMoveToCell(targetPos: Vector2) {
        clearActions()

        addAction(
            Acts.parallel(
                Acts.moveTo(targetPos.x, targetPos.y, 0.18f, Interpolation.sineOut),
                Acts.scaleTo(1f, 1f, 0.18f, Interpolation.sineOut),
                Acts.rotateTo(0f, 0.18f, Interpolation.sineOut)
            )
        )
    }

}