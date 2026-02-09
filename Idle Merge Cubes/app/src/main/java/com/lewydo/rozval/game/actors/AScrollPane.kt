package com.lewydo.rozval.game.actors

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Disposable
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.util.log

class AScrollPane(private val group: AdvancedGroup): ScrollPane(group), Disposable {

    override fun dispose() {
        group.dispose()
    }

}