package com.lewydo.rozval.game.box2d.bodies.test

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.lewydo.rozval.game.actors.AImage
import com.lewydo.rozval.game.box2d.AbstractBody
import com.lewydo.rozval.game.utils.advanced.box2d.AdvancedBox2dScreen
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.gdxGame

class BRect(override val screenBox2d: AdvancedBox2dScreen): AbstractBody() {
    override val name       = "rect"
    override val bodyDef    = BodyDef().apply {
        type = BodyDef.BodyType.StaticBody
    }
    override val fixtureDef = FixtureDef()

    override var actor: AdvancedGroup? = AImage(screenBox2d, gdxGame.assetsAll.list_PreviewLvl.first())

}