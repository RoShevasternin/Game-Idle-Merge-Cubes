package com.lewydo.idlemergecubes.game.actors.autoLayout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.lewydo.idlemergecubes.game.actors.AScrollPane
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.runGDX
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ATableGroup(
    override val screen: AdvancedScreen,
    val gapV      : Float = 0f,
    val gapH      : Float = 0f,

    val startGapV : Float = 0f,
    val startGapH : Float = 0f,

    val endGapV   : Float = 0f,
    val endGapH   : Float = 0f,

    val alignmentVV: AutoLayout.AlignmentVertical   = AutoLayout.AlignmentVertical.TOP,
    val alignmentVH: AutoLayout.AlignmentHorizontal = AutoLayout.AlignmentHorizontal.LEFT,

    val alignmentHH: AutoLayout.AlignmentHorizontal = AutoLayout.AlignmentHorizontal.LEFT,
    val alignmentHV: AutoLayout.AlignmentVertical   = AutoLayout.AlignmentVertical.BOTTOM,

    val directionV: AVerticalGroup.Static.Direction   = AVerticalGroup.Static.Direction.DOWN,
    val directionH: AHorizontalGroup.Static.Direction = AHorizontalGroup.Static.Direction.RIGHT,
) : AdvancedGroup() {

    private val verticalGroup = AVerticalGroup(screen, gapV, startGapV, endGapV, alignmentVV, alignmentVH, directionV, isWrap = true)
    private val scrollPane    = AScrollPane(verticalGroup)

    private val newHorizontalGroup get() = AHorizontalGroup(screen, gapH, startGapH, endGapH, alignmentHH, alignmentHV, directionH, isWrapV = true)
    private var tmpHorizontalGroup = newHorizontalGroup

    private var continuationAddToTable : Continuation<Unit>? = null
    private val isOutside = AtomicBoolean(false)

    override fun addActorsOnGroup() {
        verticalGroup.setSize(width, height)
        addAndFillActor(scrollPane)

        tmpHorizontalGroup.apply {
            width = verticalGroup.width
            setOutsideBlock()
        }
        verticalGroup.addActor(tmpHorizontalGroup)
    }

    // Logic ------------------------------------------------------------------------

    suspend fun addToTable(actor: Actor) = suspendCoroutine<Unit> { continuation ->
        continuationAddToTable = continuation

        runGDX {
            tmpHorizontalGroup.addActor(actor)
            verticalGroup.update()

            if (isOutside.get().not()) continuationAddToTable?.resume(Unit)
        }
    }

    private fun AHorizontalGroup.setOutsideBlock() {
        outsideBlock = { outActor ->
            isOutside.set(true)
            tmpHorizontalGroup.outsideBlock = {}
            (outActor as? AdvancedGroup)?.isDisposeOnRemove = false

            tmpHorizontalGroup.addAction(Actions.sequence(
                Actions.removeActor(outActor),
                Actions.run {
                    tmpHorizontalGroup = newHorizontalGroup.apply {
                        setOutsideBlock()
                        width = verticalGroup.width
                        addActor(outActor)
                    }
                    verticalGroup.addActor(tmpHorizontalGroup)

                    continuationAddToTable?.resume(Unit)
                    continuationAddToTable = null
                    isOutside.set(false)
                }
            ))

        }
    }

}