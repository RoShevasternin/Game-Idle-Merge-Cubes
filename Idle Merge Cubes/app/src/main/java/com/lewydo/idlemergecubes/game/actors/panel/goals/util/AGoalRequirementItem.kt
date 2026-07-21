package com.lewydo.idlemergecubes.game.actors.panel.goals.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabel
import com.lewydo.idlemergecubes.game.actors.label.AMsdfTextRow
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.grid.ACube
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
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
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleCube = MsdfStyle(msdf, msdf.fontNunitoBold, 70f)
        .stroke(1f, GameColor.brown_8D3800)
        .dropShadow(3f, 3f, 4f, GameColor.purple_350080)

    private val styleCount = MsdfStyle(msdf, msdf.fontNunitoMedium, 56f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgDoneImg      = Image(gdxGame.assetsAll.goals_bg_item_done)
    private val aBgDefImg       = Image(gdxGame.assetsAll.goals_bg_item_def)
    private val aCube           = ACube(screen, 0, 0, styleCube)
    private val aCountLbl       = AMsdfLabel("0", styleCount)
    private val aCountTargetLbl = AMsdfLabel("/5", styleCount).apply { color.a = 0.5f }
    private val aCountRow       = AMsdfTextRow(5f).add(aCountLbl).add(aCountTargetLbl)
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
        add(aCountRow) { endToEnd(margin = 20f); centerY() }

//        aCountTargetLbl.setSize(50f, 76f)
//        add(aCountTargetLbl) { endToEnd(margin = 20f); centerY() }
//
//        aCountLbl.setSize(34f, 76f)
//        add(aCountLbl) { endToStart(aCountTargetLbl, 3f); topToTop(aCountTargetLbl) }
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

        //aCountTargetLbl.pack()
        //aCountLbl.pack()

        clearActions()
        setScale(1f)

        width = (18f + aCube.width + 32f + aCountLbl.width + 3f + aCountTargetLbl.width + 20f)
    }

    // Викликати при кожній зміні прогресу
    fun updateProgress(current: Int, required: Int) {
        val done = current >= required

        aCountTargetLbl.setText("/$required")
        aCountLbl.setText("$current")

        //aCountTargetLbl.pack()
        //aCountLbl.pack()

        if (done) {
            aBgDoneImg.color.a = 1f
            aDoneImg.color.a   = 1f

            width = (18f + aCube.width + 32f + aCountRow.width + 20f + aDoneImg.width + 18f)
            this.update(aCountRow) {
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

            width = (18f + aCube.width + 32f + aCountRow.width + 20f)
            this.update(aCountRow) {
                endToStartActor = null
                endToEnd(margin = 20f)
            }
        }
    }
}