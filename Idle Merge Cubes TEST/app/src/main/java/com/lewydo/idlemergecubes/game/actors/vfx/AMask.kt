package com.lewydo.idlemergecubes.game.actors.vfx

import com.badlogic.gdx.graphics.Texture
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.vfx.MaskEffect
import com.lewydo.idlemergecubes.game.utils.vfx.VfxGroup

class AMask(
    override val screen: AdvancedScreen,
    maskTexture: Texture? = null,
) : VfxGroup(screen) {

    private val maskEffect = MaskEffect(maskTexture)

    var maskTexture: Texture?
        get()      = maskEffect.maskTexture
        set(value) { maskEffect.maskTexture = value; rerenderOnce() }

    fun rerenderStaticOnce() { rerenderOnce() }

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addEffect(maskEffect)
    }
}