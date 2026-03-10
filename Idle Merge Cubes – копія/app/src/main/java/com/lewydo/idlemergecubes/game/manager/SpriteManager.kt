package com.lewydo.idlemergecubes.game.manager

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
        LOADER  (AtlasData("atlas/loader.atlas")),
        ALL     (AtlasData("atlas/all.atlas")),
        GRID    (AtlasData("atlas/grid.atlas")),

        _9_PATCH(AtlasData("atlas/9_patch.atlas")),
    }

    enum class EnumTexture(val data: TextureData) {
        // Loader
        BACKGROUND(TextureData("textures/loader/background.png")),
        MASK      (TextureData("textures/loader/mask.png")),

        C1(TextureData("textures/loader/light/c1.png")),
        C2(TextureData("textures/loader/light/c2.png")),
        C3(TextureData("textures/loader/light/c3.png")),
        C4(TextureData("textures/loader/light/c4.png")),
        C5(TextureData("textures/loader/light/c5.png")),
        C6(TextureData("textures/loader/light/c6.png")),

        // All
        MASK_DIALOG_PROGRESS_LVL (TextureData("textures/all/mask_dialog_progress_lvl.png")),
        MASK_PROGRESS_IDLE       (TextureData("textures/all/mask_progress_idle.png")),

        // All | panel
        PANEL_TOP  (TextureData("textures/all/panel/panel_top.png")),
        PANEL_GAME (TextureData("textures/all/panel/panel_game.png")),
        PANEL_IDLE (TextureData("textures/all/panel/panel_idle.png")),

    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

}