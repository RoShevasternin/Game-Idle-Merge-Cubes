package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.label.AMsdfLabelAutoSize
import com.lewydo.idlemergecubes.game.actors.layout.autoLayout.AAutoLayout
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectPool
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.SizeScaler
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.gdxGame

class ACollectButton(
    override val screen: AdvancedScreen,
    type: Type
) : AConstraintLayout(screen) {

    override val sizeScaler = SizeScaler(SizeScaler.Axis.X, 1707f)

    private var currentDataType = type.toData()

    private var reward = "(0)"

    private val BASE_WIDTH_EFFECT = 819f

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleTitle = MsdfStyle(msdf, msdf.fontNunitoBlack, 90f)
        .stroke(2f, currentDataType.colorBorder)
        .dropShadow(7f, 5f, 4f, currentDataType.colorShadow)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBtn           = AButtonTexture(screen, AButtonStyles.Texture.COLLECT)
    private val aCenterImg     = Image(currentDataType.center)
    private val aGlareLeftImg  = Image(gdxGame.assetsAll.glare_collect_left)
    private val aGlareRightImg = Image(gdxGame.assetsAll.glare_collect_right)

    private val aHorizontalGroup = AAutoLayout(
        screen     = screen,
        gapMain    = 16f.toActual,
        alignMain  = AAutoLayout.AlignMain.CENTER,
        alignCross = AAutoLayout.AlignCross.CENTER,
        sizingW    = AAutoLayout.Sizing.HUG
    )

    private val aIconImg   = Image(currentDataType.icon)
    private val aTitleLbl  = AMsdfLabelAutoSize(
        screen       = screen,
        text         = currentDataType.title,
        style        = styleTitle,
        fitMode      = AMsdfLabelAutoSize.FitMode.HEIGHT,
        isWrapWidth  = true
    )
    private val aRewardLbl = AMsdfLabelAutoSize(
        screen       = screen,
        style        = styleTitle.apply { size = 80f },
        fitMode      = AMsdfLabelAutoSize.FitMode.HEIGHT,
        isWrapWidth  = true
    )

    private val collectEffectPool = AParticleEffectPool(gdxGame.particleEffectAll.COLLECT, maxActive = 2, minSpawnIntervalMs = 120L)

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
        aBtn.setOnClickListener(gdxGame.soundUtil.COLLECT) { blockClick.invoke() }

        val tmpPos = Vector2(x, y)
        aBtn.onTouchDown = { x, y ->
            aBtn.localToStageCoordinates(tmpPos.set(x, y))
            collectEffectPool.spawn(parent = screen.stageUI.root, x = tmpPos.x, y = tmpPos.y) {
                fitToSize(targetWidth = aBtn.width, baseWidth = BASE_WIDTH_EFFECT)
            }
        }
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

    private fun AAutoLayout.addIconImg() {
        aIconImg.setSize(this.height, this.height)
        add(aIconImg)
    }

    private fun AAutoLayout.addTitleLbl() {
        aTitleLbl.width  = 1f
        aTitleLbl.height = this.height * 0.53f // 53%
        add(aTitleLbl)
    }

    private fun AAutoLayout.addRewardLbl() {
        aTitleLbl.width   = 1f
        aRewardLbl.height = this.height * 0.48f // 48%
        add(aRewardLbl)
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