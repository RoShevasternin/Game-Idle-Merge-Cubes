package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.label.ALabelAutoFont
import com.lewydo.idlemergecubes.game.actors.layout.AlignH
import com.lewydo.idlemergecubes.game.actors.layout.AlignV
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.layout.linear.AHorizontalGroup
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.SizeScaler
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ACollectButton(
    override val screen: AdvancedScreen,
    type: Type
) : AConstraintLayout(screen) {

    override val sizeScaler = SizeScaler(SizeScaler.Axis.X, 1707f)

    private var currentDataType = type.toData()

    private var reward = "(0)"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "COLLECT X()")
        .setBorder(2f, currentDataType.colorBorder)
        .setShadow(7, 5, currentDataType.colorShadow)

    private val fontTitle  = screen.fontGenerator_Nunito_Black.generateFont(parameter.setSize(90))
    private val fontReward = screen.fontGenerator_Nunito_Black.generateFont(parameter.setSize(80))

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBtn           = AButton(screen, AButton.Type.COLLECT)
    private val aCenterImg     = Image(currentDataType.center)
    private val aGlareLeftImg  = Image(gdxGame.assetsAll.glare_collect_left)
    private val aGlareRightImg = Image(gdxGame.assetsAll.glare_collect_right)

    private val aHorizontalGroup = AHorizontalGroup(screen,
        gap = 16f.toActual,
        alignH = AlignH.CENTER,
        alignV = AlignV.CENTER,
        wrapWidth = true
    )

    private val aIconImg   = Image(currentDataType.icon)
    private val aTitleLbl  = ALabelAutoFont(
        screen       = screen,
        text         = currentDataType.title,
        labelStyle   = Label.LabelStyle(fontTitle, Color.WHITE),
        fitMode      = ALabelAutoFont.FitMode.HEIGHT,
        isWrapWidth  = true
    )
    private val aRewardLbl = ALabelAutoFont(
        screen       = screen,
        labelStyle   = Label.LabelStyle(fontReward, Color.WHITE),
        fitMode      = ALabelAutoFont.FitMode.HEIGHT,
        isWrapWidth  = true
    )

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------
    var blockClick = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBtn()
        addCenterImg()
        addGlaresImg()
        addHorizontalGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addBtn() {
        add(aBtn) { fillParent() }
        aBtn.setOnClickListener { blockClick.invoke() }
    }

    private fun addCenterImg() {
        add(aCenterImg) {
            center()
            fillWidth(0.9683f)
            fillHeight(0.8442f)
        }
        aCenterImg.disable()
    }

    private fun addGlaresImg() {
        add(aGlareLeftImg) {
            startToStart(margin = 52f.toActual)
            topToTop(margin = 19f.toActual)
            fillWidth(0.1f)
            fillHeight(0.1884f)
        }
        add(aGlareRightImg) {
            endToEnd(margin = 40f.toActual)
            bottomToBottom(margin = 39f.toActual)
            fillWidth(0.1f)
            fillHeight(0.14f)
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors - HorizontalGroup
    // ------------------------------------------------------------------------
    private fun addHorizontalGroup() {
        add(aHorizontalGroup) {
            center()
            fillHeight(0.4492f)
        }
        aHorizontalGroup.disable()

        aHorizontalGroup.apply {
            addIconImg()
            addTitleLbl()
            addRewardLbl()
        }
    }

    private fun AHorizontalGroup.addIconImg() {
        aIconImg.setSize(this.height, this.height)
        addActor(aIconImg)
    }

    private fun AHorizontalGroup.addTitleLbl() {
        aTitleLbl.height = this.height * 0.53f // 53%
        addActor(aTitleLbl)
    }

    private fun AHorizontalGroup.addRewardLbl() {
        aRewardLbl.height = this.height * 0.48f // 48%
        addActor(aRewardLbl)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    fun setReward(amount: Int) {
        reward = NumberFormatter.format(amount)
        aRewardLbl.setText("($reward)")
    }

    // ------------------------------------------------------------------------
    // classes
    // ------------------------------------------------------------------------

    data class DataType(
        val title      : String,
        val center     : TextureRegion,
        val icon       : TextureRegion,
        val colorBorder: Color,
        val colorShadow: Color,
    )

    enum class Type {
        COLLECT, COLLECT_X2;

        fun toData(): DataType = when(this) {
            COLLECT    -> DataType(
                title       = "COLLECT",
                center      = gdxGame.assetsAll.collect_center,
                icon        = gdxGame.assetsAll.coin,
                colorBorder = GameColor.brown_683E03,
                colorShadow = GameColor.brown_8D3800
            )
            COLLECT_X2 -> DataType(
                title       = "COLLECT X2",
                center      = gdxGame.assetsAll.collect_center_x2,
                icon        = gdxGame.assetsAll.x2,
                colorBorder = GameColor.brown_683E03,
                colorShadow = GameColor.brown_8D3800
            )
        }
    }


}