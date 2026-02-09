package com.lewydo.rozval.game.box2d.bodiesGroup

import com.lewydo.rozval.game.box2d.AbstractBodyGroup
import com.lewydo.rozval.game.box2d.BodyId
import com.lewydo.rozval.game.box2d.bodies.BHorizontal
import com.lewydo.rozval.game.box2d.bodies.BVertical
import com.lewydo.rozval.game.utils.SizeScaler
import com.lewydo.rozval.game.utils.advanced.box2d.AdvancedBox2dScreen

class BGBorders(override val screenBox2d: AdvancedBox2dScreen) : AbstractBodyGroup() {

    override val sizeScaler = SizeScaler(SizeScaler.Axis.X, 1842f)

    val bTop   = BHorizontal(screenBox2d)
    val bDown  = BHorizontal(screenBox2d)
    val bLeft  = BVertical(screenBox2d)
    val bRight = BVertical(screenBox2d)

    override fun create(x: Float, y: Float, w: Float, h: Float) {
        super.create(x, y, w, h)

        initB_Borders()

        createHorizontal()
        createVertical()
    }


    // Init ------------------------------------------------------------------------------------------------------

    private fun initB_Borders() {
        arrayOf(bTop, bDown, bLeft, bRight).onEach {
            it.id = BodyId.BORDERS
            // it.collisionList.addAll(BodyId.ItemId.entries.map { e -> e.name } )
        }
    }

    // ---------------------------------------------------
    // Create Body
    // ---------------------------------------------------

    private fun createHorizontal() {
        createBody(bTop, 80f, 1007f, 1686f, 66f)
        createBody(bDown, 80f, 0f, 1686f, 66f)
    }

    private fun createVertical() {
        createBody(bLeft, 0f, 80f, 66f, 913f)
        createBody(bRight, 1776f, 80f, 66f, 913f)
    }

}