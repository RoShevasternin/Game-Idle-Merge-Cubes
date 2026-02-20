package com.lewydo.idlemergecubes.game.actors.panelGame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.controller.GridController
import com.lewydo.idlemergecubes.game.model.GridModel
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.util.log
import kotlinx.coroutines.launch

class ACubeLayer(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars).setSize(264)
        .setBorder(3f, GameColor.brown_8D3800)
        .setShadow(10, 10, GameColor.purple_350080)

    private val font = screen.fontGenerator_Nunito_Bold.generateFont(parameter)

    private val cubeLabelStyle = Label.LabelStyle(font, Color.WHITE)

    private val cubes = mutableMapOf<Int, ACube>()

    override fun addActorsOnGroup() {}

    // Logic ------------------------------------------------------------------------

    fun spawnCube(index: Int, level: Int, bounds: Rectangle): ACube {
        val cube = ACube(screen, index, level, cubeLabelStyle)
        cube.setBounds(bounds.x + 5f, bounds.y + 5f, bounds.width - 10f, bounds.height - 10f)

        addActor(cube)
        cubes[index] = cube

        cube.animSpawn()

        return cube
    }

    fun removeCube(index: Int) {
        cubes[index]?.remove()
        cubes.remove(index)

    }

    fun getCube(index: Int): ACube? = cubes[index]

}