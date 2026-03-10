package com.lewydo.idlemergecubes.game.actors.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.lewydo.idlemergecubes.game.utils.actor.addAndFillActor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.runGDX

class ASaturationImage(
    override val screen: AdvancedScreen,
    var saturation: Float = 1f,
): AdvancedGroup() {

    companion object {
        private val vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
        private val fragmentShader = Gdx.files.internal("shader/saturation/saturationFS.glsl").readString()
    }

    private val image = Image()

    var drawable: Drawable = TextureRegionDrawable()
        get() = image.drawable
        set(value) {
            image.drawable = value
            field = value
        }

    constructor(screen: AdvancedScreen, region: TextureRegion) : this(screen) {
        image.drawable = TextureRegionDrawable(region)
    }
    constructor(screen: AdvancedScreen, texture: Texture) : this(screen) {
        image.drawable = TextureRegionDrawable(texture)
    }
    constructor(screen: AdvancedScreen, drawable: Drawable) : this(screen) {
        image.drawable = drawable
    }
    constructor(screen: AdvancedScreen, ninePatch: NinePatch) : this(screen) {
        image.drawable = NinePatchDrawable(ninePatch)
    }

    private var shaderProgram: ShaderProgram? = null
    private var prevShader   : ShaderProgram? = null

    override fun addActorsOnGroup() {
        runGDX {
            shaderProgram = ShaderProgram(vertexShader, fragmentShader)

            if (shaderProgram?.isCompiled?.not() == true) {
                throw IllegalStateException("Shader compilation failed:\n" + shaderProgram?.log);
            }

            addAndFillActor(image)
        }
    }

    override fun dispose() {
        shaderProgram?.dispose()
        super.dispose()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null || shaderProgram == null) return

        prevShader = batch.shader
        batch.shader = shaderProgram

        shaderProgram!!.setUniformf("u_saturation", saturation)
        shaderProgram!!.setUniformf("u_alpha", color.a * parentAlpha)

        super.draw(batch, parentAlpha)
        batch.shader = prevShader
    }

}