package com.lewydo.idlemergecubes.game.actors.panelGrid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter

class ACubeLayer(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars)
        .setSize(264)
        .setBorder(3f, GameColor.brown_8D3800)
        .setShadow(10, 10, GameColor.purple_350080)

    private val font = screen.fontGenerator_Nunito_Bold.generateFont(parameter)
    private val cubeLabelStyle = Label.LabelStyle(font, Color.WHITE)

    // ------------------------------------------------------------------------
    // Storage
    // ------------------------------------------------------------------------

    private val cubes = mutableMapOf<Int, ACube>()

    // Відступи всередині клітинки
    private val offsetX = 5f
    private val offsetY = 5f
    private val offsetW = 10f
    private val offsetH = 10f

    override fun addActorsOnGroup() {}

    // ------------------------------------------------------------------------
    // Spawn / Remove
    // ------------------------------------------------------------------------

    fun spawnCube(index: Int, level: Int, cellBounds: Rectangle): ACube {
        val cube = ACube(screen, index, level, cubeLabelStyle)

        cube.setBounds(
            cellBounds.x + offsetX,
            cellBounds.y + offsetY,
            cellBounds.width - offsetW,
            cellBounds.height - offsetH
        )

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

    fun getAllCubes(): Collection<ACube> = cubes.values

    // ------------------------------------------------------------------------
    // GAME ACTIONS (публічні)
    // ------------------------------------------------------------------------

    fun moveCube(
        from: Int,
        to: Int,
        targetCellPos: Vector2,
        onComplete: () -> Unit
    ) {
        val cube = cubes.remove(from) ?: return

        cube.index = to
        cubes[to] = cube

        val finalPos = calculateFinalPos(targetCellPos)

        animMoveTo(cube, finalPos) {
            onComplete()
        }
    }

    fun mergeCubes(
        from: Int,
        to: Int,
        targetCellPos: Vector2,
        onComplete: () -> Unit
    ) {
        val source = cubes[from] ?: return
        val target = cubes[to] ?: return

        val finalPos = calculateFinalPos(targetCellPos)

        animMerge(source, target, finalPos) {
            removeCube(from)
            target.setLevel(target.lvl + 1)

            onComplete()
        }
    }

    fun liftCube(index: Int) {
        cubes[index]?.let { cube ->
            cube.toFront()
            cube.animLift()
        }
    }

    fun moveCubeToPosition(index: Int, targetCellPos: Vector2) {
        val cube     = cubes[index] ?: return
        val finalPos = calculateFinalPos(targetCellPos)
        animMoveTo(cube, finalPos) {}
    }

    // ------------------------------------------------------------------------
    // PRIVATE VISUAL HELPERS
    // ------------------------------------------------------------------------

    private fun calculateFinalPos(cellPos: Vector2): Vector2 {
        return Vector2(
            cellPos.x + offsetX,
            cellPos.y + offsetY
        )
    }

    private fun animMoveTo(
        cube: ACube,
        finalPos: Vector2,
        onComplete: () -> Unit
    ) {
        cube.clearActions()

        cube.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(finalPos.x, finalPos.y, 0.18f, Interpolation.sineOut),
                    Actions.scaleTo(1f, 1f, 0.18f, Interpolation.sineOut),
                    Actions.rotateTo(0f, 0.18f, Interpolation.sineOut)
                ),
                Actions.run { onComplete() }
            )
        )
    }

    private fun animMerge(
        source: ACube,
        target: ACube,
        finalPos: Vector2,
        onFinish: () -> Unit
    ) {
        source.clearActions()

        source.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(finalPos.x, finalPos.y, 0.15f, Interpolation.sineIn),
                    Actions.scaleTo(0.4f, 0.4f, 0.15f)
                ),
                Actions.run { onFinish() }
            )
        )
    }

}