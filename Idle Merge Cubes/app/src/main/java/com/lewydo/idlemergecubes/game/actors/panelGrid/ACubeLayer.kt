package com.lewydo.idlemergecubes.game.actors.panelGrid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.lewydo.idlemergecubes.game.actors.label.AFlyingLabel
import com.lewydo.idlemergecubes.game.actors.label.ALabel
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectPool
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.global.GlobalStagePositions
import com.lewydo.idlemergecubes.game.utils.actor.setBounds
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACubeLayer(override val screen: AdvancedScreen): AdvancedGroup() {

    private val BASE_WIDTH_EFFECT = 381f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val parameterCube = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars)
        .setSize(264)
        .setBorder(3f, GameColor.brown_8D3800)
        .setShadow(10, 10, GameColor.purple_350080)

    private val parameterFlyingLabel = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+XP")
        .setSize(90)
        .setBorder(5f, GameColor.purple_350080)
        .setShadow(6, 6, GameColor.purple_350080)

    private val fontFlyingLabel = screen.fontGenerator_Nunito_Black.generateFont(parameterFlyingLabel)

    private val labelStyleCube = FontFactory.create(screen, parameterCube, screen.fontGenerator_Nunito_Bold)

    // ------------------------------------------------------------------------
    // Storage
    // ------------------------------------------------------------------------

    private val cubes = mutableMapOf<Int, ACube>()

    // Відступи всередині клітинки
    private val offsetX = 5f
    private val offsetY = 5f
    private val offsetW = 10f
    private val offsetH = 10f

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Particle pool
    // ------------------------------------------------------------------------

    private val mergeEffectPool = AParticleEffectPool(gdxGame.particleEffectAll.CUBE)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {}

    // ------------------------------------------------------------------------
    // Spawn / Remove
    // ------------------------------------------------------------------------

    fun spawnCube(index: Int, level: Int, cellBounds: Rectangle): ACube {
        val cube = ACube(screen, index, level, labelStyleCube)

        cube.setBounds(
            cellBounds.x + offsetX,
            cellBounds.y + offsetY,
            cellBounds.width - offsetW,
            cellBounds.height - offsetH
        )

        addActor(cube)
        cubes[index] = cube

        cube.animSpawn()
        registerTutorialPositions()
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
            registerTutorialPositions()
            onComplete()
        }
    }

    fun mergeCubes(
        from: Int,
        to: Int,
        targetCellPos: Vector2,
        xp           : Long,
        coins        : Long,
        onComplete: () -> Unit
    ) {
        val source = cubes[from] ?: return
        val target = cubes[to] ?: return

        val finalPos = calculateFinalPos(targetCellPos)

        val newLevel = target.lvl + 1

        when {
            newLevel % 10 == 0 -> {
                gdxGame.vibroUtil.vibro(75)
                gdxGame.soundUtil.apply { play(MERGE_3) }
            }
            newLevel % 2 == 0  -> {
                gdxGame.vibroUtil.vibro(40)
                gdxGame.soundUtil.apply { play(MERGE_2) }
            }
            else               -> {
                gdxGame.vibroUtil.vibro(30)
                gdxGame.soundUtil.apply { play(MERGE_1) }
            }
        }

        animMerge(source, target, finalPos) {
            removeCube(from)
            target.setLevel(newLevel)

            // спавнимо ефект в центрі target куба
            spawnMergeEffect(target, target.getVisualColor())
            spawnFlyingLabels(target, coins, xp, target.getVisualColor())

            gdxGame.tutorialManager.onMergeDone()
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

    fun clearAll() {
        cubes.values.forEach { it.remove() }
        cubes.clear()
    }

    // Апгрейд кубів після підвищення рівня BUY
    // ACube.setLevel() вже містить animUpgrade() — нічого додаткового не треба
    fun upgradeCubes(indices: List<Int>, newLevel: Int) {
        indices.forEach { index ->
            cubes[index]?.setLevel(newLevel)
        }
    }

    // ------------------------------------------------------------------------
    // Merge effect
    // ------------------------------------------------------------------------

    private fun spawnMergeEffect(target: ACube, cubeColor: Color) {
        val x = target.x + target.width  / 2f
        val y = target.y + target.height / 2f

        mergeEffectPool.spawn(parent = this, x = x, y = y) {
            setLastTintColor("star",  cubeColor)
            setFirstTintColor("wave1", cubeColor)
            setFirstTintColor("wave3", cubeColor)
            fitToSize(targetWidth = target.width, baseWidth = BASE_WIDTH_EFFECT)
        }
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

    // ------------------------------------------------------------------------
    // Flying label
    // ------------------------------------------------------------------------

    private fun spawnFlyingLabels(target: ACube, coins: Long, xp: Long, color: Color) {
        // from — центр куба в stage координатах
        val from = target.localToStageCoordinates(
            Vector2(target.width / 2f, target.height / 2f)
        )
        val size = Vector2(1f, 1f)

        val style = Label.LabelStyle(fontFlyingLabel, color)

        screen.stageUI.root.addActor(
            AFlyingLabel(
                screen = screen,
                text   = "+$coins",
                style  = style,
                to     = GlobalStagePositions.get(GlobalStagePositions.Position.COIN),
                type   = AFlyingLabel.Type.COIN,
                onEnd  = { GlobalEvents.emit(GlobalEvents.EventType.END_FLY_COIN) }
            ).also { it.setBounds(from, size) }
        )

        screen.stageUI.addActor(
            AFlyingLabel(
                screen = screen,
                text   = "+$xp XP",
                style  = style,
                to     = GlobalStagePositions.get(GlobalStagePositions.Position.XP),
                type   = AFlyingLabel.Type.XP,
                onEnd  = { GlobalEvents.emit(GlobalEvents.EventType.END_FLY_XP) }
            ).also { it.setBounds(from, size) }
        )
    }

    // ------------------------------------------------------------------------
    // Register GlobalStagePosition
    // ------------------------------------------------------------------------
    private fun registerTutorialPositions() {
        val list = cubes.values.take(2)
        if (list.size < 2) return

        list.getOrNull(0)?.let {
            val pos = it.localToStageCoordinates(Vector2(it.width / 2f, it.height / 2f))
            GlobalStagePositions.register(GlobalStagePositions.Position.CUBE_0, pos.x, pos.y)
        }
        list.getOrNull(1)?.let {
            val pos = it.localToStageCoordinates(Vector2(it.width / 2f, it.height / 2f))
            GlobalStagePositions.register(GlobalStagePositions.Position.CUBE_1, pos.x, pos.y)
        }
        gdxGame.tutorialManager.onCubePositionChanged()
    }

}