package com.lewydo.rozval.game.box2d.bodies

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.lewydo.rozval.game.actors.shader.test.ABlock
import com.lewydo.rozval.game.box2d.AbstractBody
import com.lewydo.rozval.game.box2d.BodyId
import com.lewydo.rozval.game.utils.Acts
import com.lewydo.rozval.game.utils.advanced.box2d.AdvancedBox2dScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.util.log

class BWood(override val screenBox2d: AdvancedBox2dScreen): AbstractBody() {
    override val name       = "rect"
    override val bodyDef    = BodyDef().apply {
        type = BodyDef.BodyType.DynamicBody
        linearDamping = 0.1f
    }
    override val fixtureDef = FixtureDef().apply {
        density     = 0.8f
        restitution = 0.2f
        friction    = 0.5f
    }

    override var actor: AdvancedGroup? = ABlock(screenBox2d)

    override var originalId = BodyId.BLOCK
    override val collisionList = mutableListOf(BodyId.BORDERS, BodyId.ITEM, BodyId.PERSIK, BodyId.BLOCK)

    init {
        calculateDamage()
    }

    private fun calculateDamage() {
        postSolveBlockArray.add(PostSolveBlock { contactBody, contact, impulse ->
            val velocityA = contact.fixtureA.body.linearVelocity
            val velocityB = contact.fixtureB.body.linearVelocity
            val relativeVelocity = velocityA.dst(velocityB)

            //log("relativeVelocity = $relativeVelocity")
            if (relativeVelocity < 2f) return@PostSolveBlock

            val force = impulse.normalImpulses[0]
            //log("force = $force")

            if (force > 7f) {
                val aBlock = actor as ABlock

                val damageCoff = when (contactBody.id) {
                    BodyId.ITEM    -> 1.5f // Камінь б'є сильніше
                    BodyId.BORDERS -> 0.5f // Стіни б'ють слабше
                    else -> 1.0f
                }

                val addedDamage = (force * damageCoff) / 5f // Твоя формула балансу
                val newDamage = (aBlock.getDamage() + addedDamage).coerceIn(0f, 100f)

                aBlock.updateDamage(newDamage)

                // Якщо урон 100+ — руйнуємо блок
                if (newDamage >= 100f) {
                    log("DESTROY ABlock")
                }
            }
        })
    }
}