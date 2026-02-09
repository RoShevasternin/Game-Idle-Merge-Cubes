package com.lewydo.rozval.game.actors.button.location.actors

import com.lewydo.rozval.game.actors.autoLayout.AHorizontalGroup
import com.lewydo.rozval.game.actors.autoLayout.AutoLayout
import com.lewydo.rozval.game.utils.actor.addAndFillActor
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.advanced.AdvancedScreen

class AStars3(override val screen: AdvancedScreen): AdvancedGroup() {

    private val horizontalGroup = AHorizontalGroup(screen, 7f, alignmentH = AutoLayout.AlignmentHorizontal.CENTER)
    private val starList        = List(3) { AStar(screen) }

    override fun addActorsOnGroup() {
        addAndFillActor(horizontalGroup)
        horizontalGroup.addStars()
    }

    // Actors ------------------------------------------------------------------------

    private fun AdvancedGroup.addStars() {
        starList.onEach { star ->
            star.setSize(30f, 30f)
            addActor(star)
        }
    }

    // Logic ------------------------------------------------------------------------

    fun fill(count: Int) {
        if (count <= 3) starList.take(count).onEach { star -> star.fill() }
    }

}