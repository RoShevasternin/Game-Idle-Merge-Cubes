package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.idlemergecubes.game.actors.ATmpGroup
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.GameColor
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.font.FontParameter
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfLabel
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfStyle
import com.lewydo.idlemergecubes.game.utils.font.msdf.MsdfTextRow
import com.lewydo.idlemergecubes.game.utils.font.msdf.effects.InnerShadowEffect
import com.lewydo.idlemergecubes.game.utils.gdxGame
import com.lewydo.idlemergecubes.util.log

class TestScreen : AdvancedScreen() {

    private val msdf by lazy { gdxGame.msdfManager }



    // ─── СТИЛІ: база + copy-варіації (перевірка стильової системи) ──────────
    private val sTitle by lazy {
        MsdfStyle(msdf, msdf.fontNunitoBlack, 90f, Color.valueOf("FFD34D"))
            .stroke(4f, Color.valueOf("5A2D00"))
            .dropShadow(0f, 5f, 3f, Color(0f, 0f, 0f, 0.5f))
    }
    private val sReward by lazy { sTitle.copy(size = 70f) }                       // copy: розмір
    private val sHint   by lazy {
        MsdfStyle(msdf, msdf.fontNunitoRegular, 48f, Color.valueOf("DDDDDD"))
    }
    private val sTimer  by lazy {
        MsdfStyle(msdf, msdf.fontNunitoSemiBold, 60f, Color.valueOf("9BE8FF"))
    }
    private val sClean  by lazy { sTitle.copy(color = Color.WHITE, keepEffects = false) } // copy: без ефектів

    // ─── Живі ряди ───────────────────────────────────────────────────────────
    private lateinit var rowCoins: MsdfTextRow
    private lateinit var rowTimer: MsdfTextRow
    private var coins = 9
    private var tick = 0f




    // ------------------------------------------------------------------------
    // Debug
    // ------------------------------------------------------------------------

    private val fpsFont = fontGenerator_Nunito_Black.generateFont(
        FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "FPS").setSize(100)
    )
    private val fpsLabel = Label("FPS", Label.LabelStyle(fpsFont, Color.WHITE))

    private val perfProfiler by lazy { GLProfiler(Gdx.graphics).apply { enable() } }
    private var perfFrameCounter = 0
    private val PERF_LOG_EVERY = 1   // 1 = кожен кадр; більше = рідше (throttle)}

    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(drawerUtil.getRegion(Color.valueOf("9E9E9E")))
        super.show()

        rootConstraintLayout.add(fpsLabel) {
            endToEnd(margin = 550f)
            topToTop(margin = 244f)
        }

        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        val container = ATmpGroup(this@TestScreen)
        container.setSize(WIDTH, HEIGHT)
        add(container) { center() }
        container.debug()




        stageUI.isDebugAll = true








        val LX = 120f

        // ═══ 1. ШІСТЬ ШРИФТІВ поряд (перевірка всіх атласів) ═══
        section("— 6 ШРИФТІВ Nunito —", LX, 3680f)
        var fy = 3560f
        listOf(
            "Black"     to msdf.fontNunitoBlack,
            "ExtraBold" to msdf.fontNunitoExtraBold,
            "Bold"      to msdf.fontNunitoBold,
            "SemiBold"  to msdf.fontNunitoSemiBold,
            "Medium"    to msdf.fontNunitoMedium,
            "Regular"   to msdf.fontNunitoRegular,
        ).forEach { (name, f) ->
            add(MsdfLabel(msdf, f, "$name Ago Щур 15", 58f).apply { setPosition(LX, fy) })
            fy -= 105f
        }

        // ═══ 2. СТИЛІ + COPY (права колонка зверху) ═══
        val RX = 1150f
        section("— СТИЛІ + copy() —", RX, 3680f)
        add(MsdfLabel("TITLE стиль", sTitle).apply { setPosition(RX, 3540f) })
        add(MsdfLabel("copy(70)", sReward).apply { setPosition(RX, 3380f) })
        add(MsdfLabel("copy без еф.", sClean).apply { setPosition(RX, 3250f) })
        add(MsdfLabel("hint regular", sHint).apply { setPosition(RX, 3140f) })

        // ═══ 3. ЕФЕКТИ: всі три + комбо ═══
        section("— ЕФЕКТИ —", RX, 3020f)
        add(MsdfLabel(msdf, msdf.fontNunitoBlack, "INNER", 75f, Color.valueOf("CCCCCC"))
            .addEffect(msdf.innerShadow(0f, 5f, 3f, Color(0f, 0f, 0f, 0.7f)))
            .apply { setPosition(RX, 2880f) })
        add(MsdfLabel(msdf, msdf.fontNunitoBlack, "FULL 3", 85f, Color.valueOf("FFD34D"))
            .addEffect(msdf.dropShadow(0f, 6f, 4f, Color(0f, 0f, 0f, 0.55f)))
            .addEffect(msdf.stroke(4f, Color.valueOf("5A2D00")))
            .addEffect(msdf.innerShadow(0f, 4f, 3f, Color(0.4f, 0.2f, 0f, 0.5f)))
            .apply { setPosition(RX, 2720f) })

        // ═══ 4. SPACING + LINEHEIGHT + FIGMABOX ═══
        section("— spacing / lineHeight / рамки —", LX, 2820f)
        add(MsdfLabel(msdf, msdf.fontNunitoBold, "spacing 10 gp", 58f)
            .setLetterSpacing(10f).apply { setPosition(LX, 2700f) })
        add(MsdfLabel(msdf, msdf.fontNunitoBold, "рядок 1\nрядок 2 gp", 55f)
            .setLineHeight(140f).apply { setPosition(LX, 2440f) })
        // поруч: figmaBox off (щільна) — рамки різні, порівняй
        add(MsdfLabel(msdf, msdf.fontNunitoBold, "tight gp", 58f, Color.valueOf("B7FF9E"))
            .figmaBox(false).apply { setPosition(650f, 2700f) })

        // ═══ 5. ROW: різні шрифти/розміри по baseline + ЖИВІ оновлення ═══
        section("— ROW: мікс шрифтів + живе —", LX, 2280f)
        rowCoins = MsdfTextRow(gap = 12f)
            .add(MsdfLabel("9", sReward))                              // стиль з ефектами
            .add(MsdfLabel("монет", sHint))                            // інший шрифт/розмір
        rowCoins.setPosition(LX, 2120f); stageUI.root.addActor(rowCoins)

        rowTimer = MsdfTextRow(gap = 10f)
            .add(MsdfLabel("00:00", sTimer))
            .add(MsdfLabel("до бонусу", sHint))
        rowTimer.setPosition(LX, 1980f); stageUI.root.addActor(rowTimer)

        // драбинка baseline: 3 шрифти, 3 розміри — одна лінія письма
        val ladder = MsdfTextRow(gap = 18f)
            .add(MsdfLabel(msdf, msdf.fontNunitoRegular, "Agp", 42f))
            .add(MsdfLabel(msdf, msdf.fontNunitoBold, "Agp", 75f))
            .add(MsdfLabel(msdf, msdf.fontNunitoBlack, "Agp", 115f))
            .add(MsdfLabel(msdf, msdf.fontNunitoBold, "Agp", 75f))
        ladder.setPosition(LX, 1760f); stageUI.root.addActor(ladder)

        // ═══ 6. MARKUP-кольори ═══
        section("— MARKUP —", LX, 1600f)
        msdf.fontNunitoBold.enableColorMarkup()
        add(MsdfLabel(msdf, msdf.fontNunitoBold,
            "Зібрано [#FFD34D]1500[] з [#${GameColor.white_55}]2000[] монет", 52f)
            .apply { setPosition(LX, 1480f) })

        // ═══ 7. WRAP з ефектами ═══
        section("— WRAP —", RX, 2560f)
        add(MsdfLabel(msdf, msdf.fontNunitoMedium,
            "Довгий опис що переноситься автоматично і має тінь з обводкою на кожному рядку",
            48f, Color.WHITE)
            .addEffect(msdf.dropShadow(0f, 3f, 2f, Color(0f, 0f, 0f, 0.45f)))
            .addEffect(msdf.stroke(2f, Color.valueOf("333355")))
            .apply {
                setWrap(true); setSize(850f, 380f)
                setPosition(RX, 2100f); setAlignment(Align.topLeft)
            })















//        // Вирівнювання по центру в рамці (перевірка descent/центрування)
//        add(MsdfLabel(msdf, msdf.nunitoBlack, "CENTER", 100f, Color.valueOf("B7FF9E")).apply {
//            setPosition(150f+700, 2050f); //setSize(1860f, 200f)
//            //setAlignment(Align.center)
//            pack()
//            debug()
//        })
//
//        val lbl = Label("CENTER", FontFactory.create(this@TestScreen, FontParameter().setSize(100), fontGenerator_Nunito_Black, Color.valueOf("B7FF9E")))
//        addActor(lbl)
//        lbl.setPosition(150f + 500f+700, 2050f)
//        //lbl.setAlignment(Align.center)
//        lbl.pack()
//        lbl.debug()

    }

    override fun render(delta: Float) {
        super.render(delta)
        fpsLabel.setText("${Gdx.graphics.framesPerSecond} FPS")

        // Лічильники ЗА ЦЕЙ КАДР (накопичені з reset() у кінці минулого кадру)
        val draw = perfProfiler.drawCalls
        val binds = perfProfiler.textureBindings
        val shader = perfProfiler.shaderSwitches
        val gl = perfProfiler.calls
        val frameMs = delta * 1000f

        perfFrameCounter++
        if (perfFrameCounter >= PERF_LOG_EVERY) {
            perfFrameCounter = 0
            log(
                "PERF_DIAG/frame:" +
                        " fps=${Gdx.graphics.framesPerSecond}" +
                        " ms=${"%.1f".format(frameMs)}" +
                        " draw=$draw" +
                        " binds=$binds" +
                        " shader=$shader" +
                        " gl=$gl"
            )
        }

        // КРИТИЧНО: reset у КІНЦІ кадру → наступний кадр рахується з нуля
        perfProfiler.reset()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }


    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun add(lbl: MsdfLabel) {
        stageUI.root.addActor(lbl)
    }

    // Заголовок секції через MsdfLabel (fpsFont має лише цифри+FPS)
    private fun section(title: String, x: Float, y: Float) {
        val l = MsdfLabel(msdf, msdf.fontNunitoBlack, title, 45f, Color.valueOf("222222"))
        l.setPosition(x, y)
        stageUI.root.addActor(l)
    }

}