package com.lewydo.rozval.game.utils.manager

import com.lewydo.rozval.game.actors.button.location.button.AbstractLevelButton
import com.lewydo.rozval.game.actors.button.location.separator.AbstractSeparatorButton
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen

class LocationManager(val screen: AdvancedScreen) {

    val locationSeparatorButtonManager = LocationSeparatorButtonManager(screen)
    val levelButtonManager             = LevelButtonManager(screen)

    var locationType: LocationType = LocationType.URBAN_DESERT

    fun getSeparatorButton(): AbstractSeparatorButton {
        return locationSeparatorButtonManager.getSeparatorButton(locationType)
    }

    fun getButtonList(): List<AbstractLevelButton> {
        return levelButtonManager.getButtons(locationType)
    }

    // class ------------------------------------------------------------------------

    enum class LocationType(
        val separatorName: String,
        val separatorNecessaryStars: Int,
        val separatorNecessaryKeys : Int,
        val startLevel: Int,
        val listLevelNecessaryStars: List<Int>
    ) {
        URBAN_DESERT(
            separatorName           = "Urban Desert",
            separatorNecessaryStars = 0,
            separatorNecessaryKeys  = 0,
            startLevel              = 1,
            listLevelNecessaryStars = listOf(0, 3, 5, 7, 10, 12),
        ),

        CONSTRUCTION_OASIS(
            separatorName           = "Construction Oasis",
            separatorNecessaryStars = 15,
            separatorNecessaryKeys  = 5,
            startLevel              = 7,
            listLevelNecessaryStars = listOf(15, 17),
        ),

        MOUNTAIN_LANDSCAPES(
            separatorName           = "Mountain Landscapes",
            separatorNecessaryStars = 20,
            separatorNecessaryKeys  = 10,
            startLevel              = 9,
            listLevelNecessaryStars = listOf(20, 22, 24, 27),
        ),

        TROPICAL_VOLCANOES(
            separatorName           = "Tropical Volcanoes",
            separatorNecessaryStars = 30,
            separatorNecessaryKeys  = 15,
            startLevel              = 13,
            listLevelNecessaryStars = listOf(30, 33),
        ),
    }

}