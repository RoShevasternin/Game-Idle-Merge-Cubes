package com.lewydo.idlemergecubes.game.actors.button

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.label.ALabel
import com.lewydo.idlemergecubes.game.actors.particleEffect.AParticleEffectPool
import com.lewydo.idlemergecubes.game.actors.vfx.AHslImage
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.CubeColorSystem
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.NumberFormatter
import com.lewydo.idlemergecubes.game.utils.actor.addActors
import com.lewydo.idlemergecubes.game.utils.actor.disable
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedGroup
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontFactory
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.game.utils.global.GlobalEvents
import com.lewydo.idlemergecubes.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

open class ABuyButton(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val thisRoot = this

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "BUY")
        .setShadow(7, 7, GameColor.brown_8D3800)
        .setBorder(3f, GameColor.brown_8D3800)

    private val parameterBuy   = parameter.copy().setSize(126)
    private val parameterPrice = parameter.copy().setSize(113)

    private val parameterCube = FontParameter().setCharacters(FontParameter.CharType.NUMBERS)
        .setBorder(1.5f, GameColor.brown_8D3800)
        .setSize(67)

    private val parameterUpgrade = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "BUYupgraded!Lv.")
        .setSize(90)
        .setBorder(3f, GameColor.brown_8D3800)
        .setShadow(6, 6, GameColor.brown_8D3800)

    private val fontUpgrade = screen.fontGenerator_Nunito_Black.generateFont(parameterUpgrade)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private var cubeLvl = 1

    private val BASE_WIDTH_EFFECT = 953f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aBack = ATmpGroup(screen)

    private val aContent = ATmpGroup(screen)
    private val aBuyBtn  = AButtonTexture(screen, AButtonStyles.Texture.BUY)

    private val aCubeGroup = ATmpGroup(screen)
    private val aCubeImg   = AHslImage(screen, gdxGame.assetsAll.cube_buy)
    private val aCubeLbl   = Label(cubeLvl.toString(), FontFactory.create(screen, parameterCube, screen.fontGenerator_Nunito_Black))

    private val aBuyLbl   = Label("BUY", FontFactory.create(screen, parameterBuy, screen.fontGenerator_Nunito_Black))
    private val aCoinImg  = Image(gdxGame.assetsAll.coin_with_border)
    private val aPriceLbl = Label("0", FontFactory.create(screen, parameterPrice, screen.fontGenerator_Nunito_Black))

    private val aCoinEffectPool        = AParticleEffectPool(gdxGame.particleEffectAll.BUY)
    private val aStarEffectPool        = AParticleEffectPool(gdxGame.particleEffectAll.STAR)
    private val aWaveUpgradeEffectPool = AParticleEffectPool(gdxGame.particleEffectAll.WAVE_UPGRADE)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------

    var onClick: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBack)
        addAndFillActor(aContent)
        aContent.setOrigin(Align.center)

        aContent.apply {
            addBuyBtn()
            addCubeGroup()
            addLbls()
            addCoinImg()

            children.forEach { it.disable() }
            aBuyBtn.enable()
        }

        collectBuyState()
        animIdle()
    }

    // ------------------------------------------------------------------------
    // Add Actors - aContent
    // ------------------------------------------------------------------------

    private fun AdvancedGroup.addBuyBtn() {
        addAndFillActor(aBuyBtn)

        aBuyBtn.onTouchDown = { x, y, ->
            animClick(x, y)

            aCoinEffectPool.spawn(thisRoot, x, y) {
                fitToSize(targetWidth = thisRoot.width, baseWidth = BASE_WIDTH_EFFECT)
            }
            aStarEffectPool.spawn(aBack, thisRoot.width / 2f, thisRoot.height / 2f) {
                toBack()
                fitToSize(targetWidth = thisRoot.width, baseWidth = BASE_WIDTH_EFFECT)
            }

        }

        aBuyBtn.setOnClickListener(gdxGame.soundUtil.BUY) {
            gdxGame.vibroUtil.vibro(35)
            onClick()
        }
    }

    private fun AdvancedGroup.addCubeGroup() {
        addActor(aCubeGroup)
        aCubeGroup.setBounds(580f, 110f, 165f, 165f)

        aCubeGroup.addAndFillActor(aCubeImg)
        aCubeGroup.addActor(aCubeLbl)

        aCubeImg.setColorShader(CubeColorSystem.getCubeColor(1))
        aCubeLbl.also {
            it.setBounds(25f, 35f, 41f, 92f)
            it.setOrigin(Align.center)
            it.rotation = -7f
        }
    }

    private fun AdvancedGroup.addLbls() {
        addActors(aBuyLbl, aPriceLbl)

        aBuyLbl.setBounds(777f, 115f, 265f, 172f)
        aPriceLbl.setBounds(1190f, 125f, 142f, 155f)

        aBuyLbl.setAlignment(Align.center)
    }

    private fun AdvancedGroup.addCoinImg() {
        addActor(aCoinImg)
        aCoinImg.setBounds(1080f, 149f, 95f, 90f)
    }

    // ------------------------------------------------------------------------
    // Collect
    // ------------------------------------------------------------------------

    // В collectBuyState() — додати два нових launch
    private fun collectBuyState() {
        coroutine?.launch {

            // Існуючий — ціна і стан кнопки
            launch {
                combine(
                    gdxGame.modelPlayer.coinsFlow,
                    gdxGame.modelPlayer.buyPriceFlow,
                    gdxGame.modelGrid.gridFlow
                ) { coins, price, grid ->
                    Triple(price, coins >= price, grid.any { it == 0 })
                }.collect { (price, hasMoney, hasSpace) ->
                    runGDX {
                        aPriceLbl.setText(NumberFormatter.format(price))
                        if (hasMoney && hasSpace) enable() else disable()
                    }
                }
            }

            // Оновлюємо рівень куба на кнопці
            launch {
                gdxGame.modelBuyLevel.buyLevelFlow.collect { level ->
                    runGDX { updateCubeLevel(level) }
                }
            }

            // Анімація апгрейду
            launch {
                GlobalEvents.events
                    .filter { it == GlobalEvents.EventType.BUY_LEVEL_UPGRADED }
                    .collect {
                        runGDX {
                            gdxGame.soundUtil.apply { play(BUY_UPGRADE) }
                            gdxGame.vibroUtil.vibro(100)
                            animBuyUpgrade(gdxGame.modelBuyLevel.currentBuyLevel)

                            aWaveUpgradeEffectPool.spawn(aContent, thisRoot.width / 2f, thisRoot.height / 2f) {
                                fitToSize(targetWidth = thisRoot.width, baseWidth = BASE_WIDTH_EFFECT)
                            }
                        }
                    }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animIdle() {
        aContent.clearActions()
        aContent.addAction(Actions.forever(
                Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0f, 35f, 1.1f, Interpolation.sine),
                Actions.scaleTo(1.04f, 1.04f, 1.1f, Interpolation.sine),
            ),
            Actions.parallel(
                Actions.moveBy(0f, -35f, 1.1f, Interpolation.sine),
                Actions.scaleTo(1f, 1f, 1.1f, Interpolation.sine),
            ),
        )))
    }

    fun animClick(x: Float, y: Float) {
        val normalizedX = ((x / width) - 0.5f) * 2f
        val tiltAngle   = (normalizedX * 2.5f).coerceIn(-2f, 2f)
        val pushX       = normalizedX * 15f

        aContent.originX = x.coerceIn(width * 0.25f, width * 0.75f)
        aContent.originY = height * 0.5f

        aContent.clearActions()
        aContent.addAction(
            Actions.sequence(
            Actions.parallel(
                Actions.moveBy(pushX, -12f, 0.09f, Interpolation.sine),
                Actions.scaleTo(1.03f, 0.93f, 0.09f, Interpolation.sine),
                Actions.rotateTo(tiltAngle, 0.09f, Interpolation.sine),
            ),
            Actions.parallel(
                Actions.moveBy(-pushX * 0.4f, 20f, 0.18f, Interpolation.swingOut),
                Actions.scaleTo(1.02f, 1.05f, 0.18f, Interpolation.swingOut),
                Actions.rotateTo(tiltAngle * -0.2f, 0.16f, Interpolation.swingOut),
            ),
            Actions.parallel(
                Actions.moveTo(0f, 0f, 0.25f, Interpolation.sine),  // ← завжди точно в (0,0)
                Actions.scaleTo(1f, 1f, 0.25f, Interpolation.swingOut),
                Actions.rotateTo(0f, 0.20f, Interpolation.sine),
            ),
            Actions.run {
                aContent.setOrigin(Align.center)
                animIdle()
            }
        ))
    }

    private fun animBuyUpgrade(newLevel: Int) {
        val lbl = ALabel(screen, "BUY upgraded! Lv.$newLevel", Label.LabelStyle(fontUpgrade, Color.GOLD))
        lbl.pack()
        lbl.setOrigin(Align.center)
        lbl.setPosition((width - lbl.width) / 2f, height)
        lbl.setScale(0f)
        lbl.color.a = 0f
        addActor(lbl)

        lbl.addAction(Actions.sequence(
            // Поява — повільніше і плавніше
            Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.4f, Interpolation.swingOut),
                Actions.fadeIn(0.35f),
            ),
            // Підлітає вгору і зависає
            Actions.parallel(
                Actions.moveBy(0f, 100f, 1.6f, Interpolation.sineOut),
            ),
            // Зависає — пауза щоб юзер встиг прочитати
            Actions.delay(0.8f),
            // Набирає силу — стискається і опускається
            Actions.parallel(
                Actions.scaleTo(1.2f, 0.8f, 0.22f, Interpolation.sineIn),
                Actions.moveBy(0f, -20f, 0.22f, Interpolation.sineIn),
            ),
            // Імпульс вгору — плавніше зникнення
            Actions.parallel(
                Actions.moveBy(0f, 150f, 0.6f, Interpolation.sineIn),
                Actions.scaleTo(0.7f, 1.2f, 0.18f, Interpolation.sineOut),
                Actions.sequence(
                    Actions.delay(0.15f),
                    Actions.scaleTo(0f, 0f, 0.4f, Interpolation.sineIn),
                ),
                Actions.sequence(
                    Actions.delay(0.1f),
                    Actions.fadeOut(0.5f, Interpolation.sineIn),
                ),
            ),
            Actions.run { lbl.remove() }
        ))
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun updateCubeLevel(level: Int) {
        cubeLvl = level
        aCubeLbl.setText(level.toString())
        aCubeImg.setColorShader(CubeColorSystem.getCubeColor(level))
    }

    // ------------------------------------------------------------------------
    // enable / disable
    // ------------------------------------------------------------------------

    fun enable() {
        if (touchable == Touchable.enabled) return
        touchable = Touchable.enabled
        animIdle()

        aBuyBtn.enable()
    }

    fun disable() {
        if (touchable == Touchable.disabled) return
        touchable = Touchable.disabled

        aContent.clearActions()
        aContent.addAction(
            Actions.parallel(
            Actions.moveTo(0f, 0f, 0.15f, Interpolation.sineOut),
            Actions.scaleTo(1f, 1f, 0.15f, Interpolation.sineOut),
            Actions.rotateTo(0f, 0.15f, Interpolation.sineOut),
        ))

        aBuyBtn.disable()
    }

}