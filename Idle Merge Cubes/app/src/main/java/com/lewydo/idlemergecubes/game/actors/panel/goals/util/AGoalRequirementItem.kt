package com.lewydo.idlemergecubes.game.actors.panel.goals.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.grid.ACube
import com.lewydo.idlemergecubes.game.actors.vfx.AHslImage
import com.lewydo.idlemergecubes.game.utils.CubeColorSystem
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  AGoalRequirementItem
//  Один елемент Combined/Timed задачі: [Куб N] current/required або ✓
// ═════════════════════════════════════════════════════════════════════════════

class AGoalRequirementItem(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Sizes
    // ------------------------------------------------------------------------
    private val CUBE_SIZE = 100f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterCube  = FontParameter().setCharacters(FontParameter.CharType.NUMBERS).setSize(70).setBorder(1f, GameColor.brown_8D3800).setShadow(3, 3, GameColor.purple_350080)
    private val parameterCount = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "/").setSize(56)

    private val lsCube  = FontFactory.create(screen, parameterCube, screen.fontGenerator_Nunito_Black, Color.WHITE)
    private val lsCount = FontFactory.create(screen, parameterCount, screen.fontGenerator_Nunito_Medium, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgDoneImg      = Image(gdxGame.assetsAll.goals_bg_item_done)
    private val aBgDefImg       = Image(gdxGame.assetsAll.goals_bg_item_def)
    private val aCube           = ACube(screen, 0, 0, lsCube)
    private val aCountLbl       = Label("0", lsCount)
    private val aCountTargetLbl = Label("/5", lsCount).apply { color.a = 0.5f }
    private val aDoneImg        = Image(gdxGame.assetsAll.goals_item_done)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addBg()
        addCube()
        addCountLbl()
        addDoneImg()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aBgDoneImg.setSize(width + 12f, height + 12f)
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBg() {
        aBgDoneImg.color.a = 0f
        add(aBgDoneImg) { center() }

        add(aBgDefImg) { fillParent() }
    }

    private fun addCube() {
        aCube.setSize(CUBE_SIZE, CUBE_SIZE)
        add(aCube) { startToStart(margin = 18f); centerY() }
    }

    private fun addCountLbl() {
        aCountTargetLbl.setSize(50f, 76f)
        add(aCountTargetLbl) { endToEnd(margin = 20f); centerY() }

        aCountLbl.setSize(34f, 76f)
        add(aCountLbl) { endToStart(aCountTargetLbl, 3f); topToTop(aCountTargetLbl) }
    }

    private fun addDoneImg() {
        aDoneImg.color.a = 0f
        aDoneImg.setSize(50f, 50f)
        add(aDoneImg) { endToEnd(margin = 18f); centerY() }
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    // Викликати при отриманні нової задачі
    fun setRequirement(level: Int, count: Int) {
        aBgDoneImg.color.a = 0f

        aCube.setLevel(level)

        aCountTargetLbl.setText("/$count")
        aCountLbl.setText("0")

        aCountTargetLbl.pack()
        aCountLbl.pack()

        clearActions()
        setScale(1f)

        width = (18f + aCube.width + 32f + aCountLbl.width + 3f + aCountTargetLbl.width + 20f)
    }

    // Викликати при кожній зміні прогресу
    fun updateProgress(current: Int, required: Int) {
        val done = current >= required

        aCountTargetLbl.setText("/$required")
        aCountLbl.setText("$current")

        aCountTargetLbl.pack()
        aCountLbl.pack()

        if (done) {
            aBgDoneImg.color.a = 1f
            aDoneImg.color.a   = 1f

            width = (18f + aCube.width + 32f + aCountLbl.width + 3f + aCountTargetLbl.width + 20f + aDoneImg.width + 18f)
            this.update(aCountTargetLbl) {
                endToEndActor = null
                endToStart(aDoneImg, 20f)
            }
            this.layout()

            // Пульс анімація при виконанні
            setOrigin(Align.center)
            clearActions()
            addAction(Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 0.15f, Interpolation.sineOut),
                Actions.scaleTo(1.0f, 1.0f, 0.20f, Interpolation.sineOut),
            ))
        } else {
            aBgDoneImg.color.a = 0f
            aDoneImg.color.a   = 0f

            width = (18f + aCube.width + 32f + aCountLbl.width + 3f + aCountTargetLbl.width + 20f)
            this.update(aCountTargetLbl) {
                endToStartActor = null
                endToEnd(margin = 20f)
            }
        }
    }
}