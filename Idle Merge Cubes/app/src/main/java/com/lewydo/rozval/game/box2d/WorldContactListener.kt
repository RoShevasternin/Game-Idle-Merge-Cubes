package com.lewydo.rozval.game.box2d

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.Array
import com.lewydo.rozval.game.manager.util.SoundUtil
import com.lewydo.rozval.game.utils.currentTimeMinus
import com.lewydo.rozval.game.utils.gdxGame
import com.lewydo.rozval.util.log

class WorldContactListener: ContactListener {

    private var timeContactBorders = 0L

    private val Contact.abstractBodyA get() = (fixtureA.body.userData as AbstractBody)
    private val Contact.abstractBodyB get() = (fixtureB.body.userData as AbstractBody)

    private val tmpArray = Array<AbstractBody>().apply { setSize(2) }
    private lateinit var soundUtil: SoundUtil

    var beginContactBlockArray = Array<ContactBlock>()
    var endContactBlockArray   = Array<ContactBlock>()
    var preSolveBlockArray     = Array<PreSolveBlock>()
    var postSolveBlockArray    = Array<PostSolveBlock>()

    override fun beginContact(contact: Contact) {
        with(contact) {
            playSound(abstractBodyA, abstractBodyB)
            abstractBodyA.beginContact(abstractBodyB, contact)
            abstractBodyB.beginContact(abstractBodyA, contact)

            beginContactBlockArray.forEach { it.block(abstractBodyA, abstractBodyB, contact) }
        }
    }

    override fun endContact(contact: Contact) {
        with(contact) {
            abstractBodyA.endContact(abstractBodyB, contact)
            abstractBodyB.endContact(abstractBodyA, contact)

            endContactBlockArray.forEach { it.block(abstractBodyA, abstractBodyB, contact) }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold?) {
        oldManifold ?: return

        with(contact) {
            abstractBodyA.preSolve(abstractBodyB, contact, oldManifold)
            abstractBodyB.preSolve(abstractBodyA, contact, oldManifold)

            preSolveBlockArray.forEach { it.block(abstractBodyA, abstractBodyB, contact, oldManifold) }
        }

//        oldManifold?.apply {
//            log(""" preSolve:
//                ${this.type}
//                ${this.points.joinToString()}
//                ${this.localPoint}
//                ${this.pointCount}
//                ${this.localNormal}
//            """)
//        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse?) {
        impulse ?: return

        with(contact) {
            abstractBodyA.postSolve(abstractBodyB, contact, impulse)
            abstractBodyB.postSolve(abstractBodyA, contact, impulse)

            postSolveBlockArray.forEach { it.block(abstractBodyA, abstractBodyB, contact, impulse) }
        }

//        impulse?.apply {
//            log(""" postSolve:
//                ${this.count}
//                ${this.normalImpulses.sum()}
//                ${this.tangentImpulses.joinToString()}
//            """)
//        }
    }

    // ---------------------------------------------------
    // Logic
    // ---------------------------------------------------

    private fun playSound(bodyA: AbstractBody, bodyB: AbstractBody) {
        tmpArray[0] = bodyA
        tmpArray[1] = bodyB

        soundUtil = gdxGame.soundUtil

        if (tmpArray.all { it.fixtureDef.isSensor.not() }) {
            when {
                tmpArray.any { it.id == BodyId.BORDERS }
                -> if (currentTimeMinus(timeContactBorders) >= 210) {
                    //soundUtil.apply { play(boomList.random()) }
                    timeContactBorders = System.currentTimeMillis()
                }
            }
        }

    }

    fun interface ContactBlock { fun block(bodyA: AbstractBody, bodyB: AbstractBody, contact: Contact) }
    fun interface PreSolveBlock { fun block(bodyA: AbstractBody, bodyB: AbstractBody, contact: Contact, manifold: Manifold) }
    fun interface PostSolveBlock { fun block(bodyA: AbstractBody, bodyB: AbstractBody, contact: Contact, impulse: ContactImpulse) }

}