package com.lewydo.rozval.game.box2d.bodies

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.lewydo.rozval.game.actors.AImage
import com.lewydo.rozval.game.box2d.AbstractBody
import com.lewydo.rozval.game.box2d.BodyId
import com.lewydo.rozval.game.utils.advanced.box2d.AdvancedBox2dScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.gdxGame

class BBrick(override val screenBox2d: AdvancedBox2dScreen): AbstractBody() {
    override val name       = "brick"
    override val bodyDef    = BodyDef().apply {
        type = BodyDef.BodyType.DynamicBody
    }
    override val fixtureDef = FixtureDef().apply {
        density     = 1f
        restitution = 0.5f
        friction    = 0.5f
    }

    override var actor: AdvancedGroup? = AImage(screenBox2d, gdxGame.assetsAll.BRICK)

    override var originalId = BodyId.ITEM
    override val collisionList = mutableListOf(BodyId.PERSIK, BodyId.BORDERS, BodyId.BLOCK)
}