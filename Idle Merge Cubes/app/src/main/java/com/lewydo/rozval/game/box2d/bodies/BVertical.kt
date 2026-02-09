package com.lewydo.rozval.game.box2d.bodies

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.lewydo.rozval.game.actors.AImage
import com.lewydo.rozval.game.box2d.AbstractBody
import com.lewydo.rozval.game.box2d.BodyId
import com.lewydo.rozval.game.utils.advanced.box2d.AdvancedBox2dScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.gdxGame

class BVertical(override val screenBox2d: AdvancedBox2dScreen): AbstractBody() {
    override val name       = "ver"
    override val bodyDef    = BodyDef().apply {
        type = BodyDef.BodyType.StaticBody
    }
    override val fixtureDef = FixtureDef()

    override var actor: AdvancedGroup? = AImage(screenBox2d, gdxGame.assetsAll.VER)

    override var originalId = BodyId.BORDERS
    override val collisionList = mutableListOf(BodyId.PERSIK, BodyId.ITEM, BodyId.BLOCK)
}