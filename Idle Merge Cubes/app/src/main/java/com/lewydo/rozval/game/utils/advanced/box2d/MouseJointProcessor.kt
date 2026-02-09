package com.lewydo.rozval.game.utils.advanced.box2d

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.QueryCallback
import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.lewydo.rozval.game.box2d.AbstractBody
import com.lewydo.rozval.game.box2d.AbstractJoint
import com.lewydo.rozval.game.box2d.BodyId
import com.lewydo.rozval.game.box2d.WorldUtil
import com.lewydo.rozval.game.box2d.bodies.standart.BStatic
import com.lewydo.rozval.game.utils.currentTimeMinus
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.game.utils.scaledToWorld

class MouseJointProcessor(
    private val world: WorldUtil,
    private val viewportWorld: Viewport,
    private val bStatic: BStatic,
    private val jMouse: AbstractJoint<MouseJoint, MouseJointDef>
) : InputAdapter() {

    private var hitAbstractBody: AbstractBody? = null
    private val touchPointInWorld = Vector2()
    private val tmpVector2        = Vector2()

    private val callback = QueryCallback { fixture ->
        if (
            fixture.isSensor.not() &&
            fixture.testPoint(touchPointInWorld) &&
            (fixture.body?.userData as AbstractBody).id != BodyId.NONE
        ) {
            hitAbstractBody = fixture.body?.userData as AbstractBody
            return@QueryCallback false
        }
        true
    }

    private var timeTouchDown = 0L

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer != 0) return false

        // ВАЖЛИВО: перетворюємо екранні координати в координати світу Box2D
        // Враховуємо зум та позицію камери світу!
        viewportWorld.unproject(tmpVector2.set(screenX.toFloat(), screenY.toFloat()))
        touchPointInWorld.set(tmpVector2.scaledToWorld)

        hitAbstractBody = null
        world.world.QueryAABB(callback,
            touchPointInWorld.x - 0.01f,
            touchPointInWorld.y - 0.01f,
            touchPointInWorld.x + 0.01f,
            touchPointInWorld.y + 0.01f)

        if (hitAbstractBody != null) {
            playSound_TouchDown()
            jMouse.create(MouseJointDef().apply {
                bodyA = bStatic.body
                bodyB = hitAbstractBody!!.body
                collideConnected = true

                target.set(touchPointInWorld)

                maxForce     = 1000f * bodyB.mass
                frequencyHz  = 5.0f
                dampingRatio = 0.7f
            })
            return true // Подія поглинута, камера не рухатиметься
        }
        return false // Пропускаємо подію до камери
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (pointer != 0 || jMouse.joint == null) return false

        viewportWorld.unproject(tmpVector2.set(screenX.toFloat(), screenY.toFloat()))
        jMouse.joint?.target = tmpVector2.scaledToWorld
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer != 0 || jMouse.joint == null) return false
        playSound_TouchUp()
        jMouse.destroy()
        return true
    }

    private fun playSound_TouchDown() {
        if (currentTimeMinus(timeTouchDown) >= 202) {
            gdxGame.soundUtil.apply { play(click) }
            timeTouchDown = System.currentTimeMillis()
        }
    }

    private fun playSound_TouchUp() {
        if (currentTimeMinus(timeTouchDown) >= 405) gdxGame.soundUtil.apply { play(click) }
    }
}