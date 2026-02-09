package com.lewydo.rozval.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class SpriteManager(var assetManager: AssetManager) {

    var loadableAtlasList   = mutableListOf<AtlasData>()
    var loadableTexturesList   = mutableListOf<TextureData>()

    fun loadAtlas() {
        loadableAtlasList.onEach { assetManager.load(it.path, TextureAtlas::class.java) }
    }

    fun initAtlas() {
        loadableAtlasList.onEach { it.atlas = assetManager[it.path, TextureAtlas::class.java] }
        loadableAtlasList.clear()
    }

    // Texture
    fun loadTexture() {
        loadableTexturesList.onEach { assetManager.load(it.path, Texture::class.java) }
    }

    fun initTexture() {
        loadableTexturesList.onEach { it.texture = assetManager[it.path, Texture::class.java] }
        loadableTexturesList.clear()
    }

    fun initAtlasAndTexture() {
        initAtlas()
        initTexture()
    }


    enum class EnumAtlas(val data: AtlasData) {
        LOADER(AtlasData("atlas/loader.atlas")),
        THANKS(AtlasData("atlas/thanks.atlas")),

        ALL      (AtlasData("atlas/all.atlas")      ),
        LEVEL_BTN(AtlasData("atlas/level_btn.atlas")),
    }

    enum class EnumTexture(val data: TextureData) {
        PANEL        (TextureData("textures/all/panel.png")        ),
        PROGRESS_MASK(TextureData("textures/all/progress_mask.png")),

        LEVEL_SEPARATOR           (TextureData("textures/all/level_separator.png")),
        LEVEL_SEPARATOR_LOCK_DEF  (TextureData("textures/all/level_separator_lock_def.png")),
        LEVEL_SEPARATOR_LOCK_PRESS(TextureData("textures/all/level_separator_lock_press.png")),

        LVL_1(TextureData("textures/level_backgrounds/LVL 1.png")),


        // TEST
        MASK_CIRCLE (TextureData("textures/TEST/mask_circle.png")),
        HOR         (TextureData("textures/TEST/hor.png")),
        VER         (TextureData("textures/TEST/ver.png")),
        BRICK       (TextureData("textures/TEST/Brick.png")),
        WOOD        (TextureData("textures/TEST/wood.png")),
        RESET       (TextureData("textures/TEST/reset.png")),
        GRID        (TextureData("textures/TEST/16x9.png")),
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}