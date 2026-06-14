package com.lewydo.idlemergecubes.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class SpriteManager(var assetManager: AssetManager) {

    var loadableAtlasList    = mutableListOf<AtlasData>()
    var loadableTexturesList = mutableListOf<TextureData>()
    var loadableGroupList    = mutableListOf<TextureGroupData>()

    // ------------------------------------------------------------------------
    // Atlas
    // ------------------------------------------------------------------------
    fun loadAtlas() {
        loadableAtlasList.onEach { assetManager.load(it.path, TextureAtlas::class.java) }
    }

    fun initAtlas() {
        loadableAtlasList.onEach { it.atlas = assetManager[it.path, TextureAtlas::class.java] }
        loadableAtlasList.clear()
    }

    // ------------------------------------------------------------------------
    // Texture
    // ------------------------------------------------------------------------
    fun loadTexture() {
        loadableTexturesList.onEach { assetManager.load(it.path, Texture::class.java) }
    }

    fun initTexture() {
        loadableTexturesList.onEach { it.texture = assetManager[it.path, Texture::class.java] }
        loadableTexturesList.clear()
    }

    // ------------------------------------------------------------------------
    // TextureGroup
    // ------------------------------------------------------------------------
    fun loadGroups() {
        loadableGroupList.onEach { group -> group.paths.forEach { assetManager.load(it, Texture::class.java) } }
    }

    fun initGroups() {
        loadableGroupList.onEach { group -> group.textures = group.paths.map { assetManager[it, Texture::class.java] } }
        loadableGroupList.clear()
    }

    // ------------------------------------------------------------------------
    // Util
    // ------------------------------------------------------------------------
    fun initAll() {
        initAtlas()
        initTexture()
        initGroups()
    }

    // ------------------------------------------------------------------------
    // EnumAtlas
    // ------------------------------------------------------------------------
    enum class EnumAtlas(val data: AtlasData) {
        BRAND(AtlasData("atlas/brand.atlas")),

        LOADER(AtlasData("atlas/loader.atlas")),

        ALL     (AtlasData("atlas/all.atlas")),
        GRID    (AtlasData("atlas/grid.atlas")),
        MENU    (AtlasData("atlas/menu.atlas")),

        _9_PATCH(AtlasData("atlas/9_patch.atlas")),
    }

    // ------------------------------------------------------------------------
    // EnumTexture
    // ------------------------------------------------------------------------
    enum class EnumTexture(val data: TextureData) {
        //bg_test(TextureData("textures/bg_test.png")),
        green(TextureData("textures/green.png")),
        red(TextureData("textures/red.png")),
        ComingSoon(TextureData("textures/Coming Soon.png")),

        // Band
        BRAND_BACKGROUND(TextureData("textures/brand/background.png")),

        // Loader
        BACKGROUND(TextureData("textures/loader/background.png")),
        MASK(TextureData("textures/loader/mask.png")),

        // All
        LIGHT    (TextureData("textures/all/LIGHT.png")),
        COIN_BIG (TextureData("textures/all/coin_big.png")),
        CONFETTI(TextureData("textures/all/confetti.png")),

        // All | brand
        BRAND      (TextureData("textures/all/brand/brand.png")),
        BRAND_BACK (TextureData("textures/all/brand/brand_back.png")),
        BRAND_FRONT(TextureData("textures/all/brand/brand_front.png")),
        LILY       (TextureData("textures/all/brand/lily.png")),
        VELDAN     (TextureData("textures/all/brand/veldan.png")),

        // All | mask
        MASK_DIALOG_PROGRESS_LVL (TextureData("textures/all/mask/mask_dialog_progress_lvl.png")),
        MASK_PROGRESS_MERGE_BONUS(TextureData("textures/all/mask/mask_progress_merge_bonus.png")),
        MASK_DIALOG_OFFLINE      (TextureData("textures/all/mask/mask_dialog_offline.png")),
        MASK_DIALOG_LEVEL_UP     (TextureData("textures/all/mask/mask_dialog_level_up.png")),
        MASK_PROGRESS_BUY_HINT   (TextureData("textures/all/mask/mask_progress_buy_hint.png")),
        MASK_GOALS_PROGRESS      (TextureData("textures/all/mask/mask_goals_progress.png")),

        // All | panel
        PANEL_TOP           (TextureData("textures/all/panel/panel_top.png")),
        PANEL_GAME          (TextureData("textures/all/panel/panel_game.png")),
        PANEL_MENU          (TextureData("textures/all/panel/panel_menu.png")),
        PANEL_LEVEL_UP_BONUS(TextureData("textures/all/panel/panel_level_up_bonus.png")),

        // All | dialog
        DIALOG_CLEAR_GRID   (TextureData("textures/all/dialog/dialog_clear_grid.png")),
        DIALOG_OFFLINE      (TextureData("textures/all/dialog/dialog_offline.png")),
        DIALOG_LEVEL_UP     (TextureData("textures/all/dialog/dialog_level_up.png")),

        // All | goals
        BG_COMBINED (TextureData("textures/all/goals/bg_combined.png")),
        BG_SIMPLE   (TextureData("textures/all/goals/bg_simple.png")),
        BG_TIMED    (TextureData("textures/all/goals/bg_timed.png")),
    }

    // ------------------------------------------------------------------------
    // EnumTextureGroup
    // ------------------------------------------------------------------------
    enum class EnumTextureGroup(
        private val folder: String,
        private val prefix: String,
        private val count : Int,
        private val separator: String = "_",
    ) {
        LIGHT_C("textures/loader/light/", "c", 6, ""),

        ;
        val data: TextureGroupData by lazy {
            TextureGroupData((1..count).map { "$folder/$prefix$separator$it.png" })
        }
    }

    data class AtlasData(val path: String) {
        lateinit var atlas: TextureAtlas
    }

    data class TextureData(val path: String) {
        lateinit var texture: Texture
    }

    data class TextureGroupData(val paths: List<String>) {
        lateinit var textures: List<Texture>
    }

}