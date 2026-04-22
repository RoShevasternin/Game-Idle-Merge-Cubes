package com.lewydo.idlemergecubes.game.utils.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.Disposable
import com.lewydo.idlemergecubes.game.utils.disposeAll

class FontGenerator(fontPath: FontPath): FreeTypeFontGenerator(Gdx.files.internal(fontPath.path)) {

    private val fontCache = mutableMapOf<String, BitmapFont>()

    override fun generateFont(parameter: FreeTypeFontParameter): BitmapFont {
        val key  = buildCacheKey(parameter)
        val font = fontCache.getOrPut(key) { super.generateFont(parameter) }

        return font
    }

    private fun buildCacheKey(p: FreeTypeFontParameter): String {
        return "${p.size}_${p.borderWidth}_${p.borderColor}_${p.shadowOffsetX}_${p.shadowOffsetY}_${p.characters.length}"
    }

    override fun dispose() {
        super.dispose()
        fontCache.values.disposeAll()
        fontCache.clear()
    }

    companion object {
        enum class FontPath(val path: String) {
            Nunito_Black     ("font/Nunito-Black.ttf"),
            Nunito_Bold      ("font/Nunito-Bold.ttf"),
            Nunito_ExtraBold ("font/Nunito-ExtraBold.ttf"),
            Nunito_Regular   ("font/Nunito-Regular.ttf"),
            Nunito_SemiBold  ("font/Nunito-SemiBold.ttf"),
        }
    }

}