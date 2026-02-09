package com.lewydo.rozval.game.utils.advanced.box2d

import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef
import com.lewydo.rozval.game.box2d.AbstractJoint
import com.lewydo.rozval.game.box2d.WorldUtil
import com.lewydo.rozval.game.box2d.bodies.standart.BStatic
import com.lewydo.rozval.game.utils.addProcessors

abstract class AdvancedBox2dUserScreen: AdvancedBox2dScreen(WorldUtil()) {

    private val jMouse by lazy { AbstractJoint<MouseJoint, MouseJointDef>(this) }
    private val bStatic by lazy { BStatic(this) }

    private val mouseJointProcessor by lazy {
        MouseJointProcessor(worldUtil, viewportWorld, bStatic, jMouse)
    }

    override fun show() {
        super.show()
        createB_Static()

        inputMultiplexer.clear()
        inputMultiplexer.addProcessors(
            this,                 // Системні клавіші
            stageUI,              // Кнопки (найвищий пріоритет)
            mouseJointProcessor,   // Фізика (MouseJoint)
            gestureDetector,       // Камера (GestureDetector)
            stageWorld,
            stageBack
        )
    }

    // Bodies ------------------------------------------------------------------------

    private fun createB_Static() {
        bStatic.create(0f, 0f,1f,1f)
    }

}