package com.lewydo.idlemergecubes.game.actors.panel.goals

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.utils.CubeColorSystem
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.actors.vfx.AHslImage

// ═════════════════════════════════════════════════════════════════════════════
//  AGoalRequirementItem
//  Один елемент Combined/Timed задачі: [Куб N] current/required або ✓
// ═════════════════════════════════════════════════════════════════════════════

class AGoalRequirementItem(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------

    private val paramLevel = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(65)
        .setShadow(4, 4, Color.BLACK)

    private val paramCount = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "/✓")
        .setSize(52)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aBorderImg  = Image(screen.drawerUtil.getTexture(GameColor.white_55))
    private val aCubeImg    = AHslImage(screen, gdxGame.assetsAll.cube)
    private val aLevelLbl   = Label("", FontFactory.create(screen, paramLevel, screen.fontGenerator_Nunito_Black, Color.WHITE))
    private val aCountLbl   = Label("", FontFactory.create(screen, paramCount, screen.fontGenerator_Nunito_SemiBold, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun addActorsOnGroup() {
        addActor(aBorderImg)
        aBorderImg.setBounds(0f, 0f, width, height)
        aBorderImg.color.a = 0.12f

        addActor(aCubeImg)
        aCubeImg.setBounds(16f, 28f, 135f, 135f)

        addActor(aLevelLbl)
        aLevelLbl.setBounds(24f, 50f, 119f, 86f)
        aLevelLbl.setAlignment(Align.center)

        addActor(aCountLbl)
        aCountLbl.setBounds(165f, 52f, 240f, 70f)
    }

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    // Викликати при отриманні нової задачі
    fun setRequirement(level: Int, count: Int) {
        aCubeImg.setColorShader(CubeColorSystem.getCubeColor(level))
        aLevelLbl.setText(level.toString())
        aCountLbl.setText("0/$count")
        aCountLbl.color = Color.WHITE
        aBorderImg.color.set(1f, 1f, 1f, 0.12f)
        clearActions()
        setScale(1f)
    }

    // Викликати при кожній зміні прогресу
    fun updateProgress(current: Int, required: Int) {
        val done = current >= required

        if (done) {
            aCountLbl.setText("✓")
            aCountLbl.color = GameColor.green_98FF68
            aBorderImg.color.set(Color.valueOf("22BB55"))
            aBorderImg.color.a = 0.35f

            // Пульс анімація при виконанні
            clearActions()
            addAction(Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 0.15f, Interpolation.sineOut),
                Actions.scaleTo(1.0f, 1.0f, 0.20f, Interpolation.sineOut),
            ))
        } else {
            aCountLbl.setText("$current/$required")
            aCountLbl.color = Color.WHITE
            aBorderImg.color.set(1f, 1f, 1f, 0.12f)
        }
    }
}