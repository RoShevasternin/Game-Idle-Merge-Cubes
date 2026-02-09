//package com.lewydo.rozval.game.actors
//
//import com.badlogic.gdx.math.Interpolation
//import com.badlogic.gdx.scenes.scene2d.actions.Actions
//import com.badlogic.gdx.scenes.scene2d.ui.Image
//import com.badlogic.gdx.scenes.scene2d.ui.Label
//import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
//import com.badlogic.gdx.utils.Align
//import com.lewydo.rozval.R
//import com.lewydo.rozval.game.actors.mask.InvertedMask
//import com.lewydo.rozval.game.utils.GameColor
//import com.lewydo.rozval.game.utils.Layout.Tutorial.Bonus
//import com.lewydo.rozval.game.utils.Layout.Tutorial.Levels
//import com.lewydo.rozval.game.utils.Layout.Tutorial.Menu
//import com.lewydo.rozval.game.utils.Layout.Tutorial.Rules
//import com.lewydo.rozval.game.utils.actor.*
//import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
//import com.lewydo.rozval.game.utils.advanced.AdvancedScreen
//import com.lewydo.rozval.game.utils.font.FontParameter
//
//class ATutorials(override val screen: AdvancedScreen) : AdvancedGroup() {
//
//    companion object {
//        private var stepIndex = 0
//
//        val STEP get() = Static.Step.entries[stepIndex]
//
//        fun nextStep() {
//            if (stepIndex < Static.Step.entries.lastIndex) stepIndex++
//        }
//    }
//
//    private val fontParameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(50)
//    private val font          = screen.fontGenerator_MiltonianTattoo.generateFont(fontParameter)
//
//    private val maskRegion     = screen.drawerUtil.getRegion(GameColor.tutorial.cpy().apply { a = 0.65f })
//    private val mask           = InvertedMask(screen, maskRegion)
//    private val visibleAreaImg = Image(screen.game.assetsAll.visible_area)
//    private val handImg        = Image(screen.game.assetsAll.hand).apply { this.disable() }
//    private val textLbl        = Label("", Label.LabelStyle(font, GameColor.golden))
//    private val nextImg        = Image(screen.game.assetsAll.next)
//    private val startGameImg   = Image(screen.game.assetsAll.start_game)
//    private val backgroundImg  = Image(screen.game.assetsAll.b_music)
//
//    // Field
//    var rulesBackBlock      = {}
//    var menuBonusBlock      = {}
//    var bonusGoBlock        = {}
//    var levelsUnlockedBlock = {}
//    var gameStartGameBlock  = {}
//
//    override fun addActorsOnGroup() {
//        disable()
//
//        when(STEP) {
//            Static.Step.MenuRules       -> addMenuRules()
//            Static.Step.RulesNext       -> addRulesNext()
//            Static.Step.MenuSettings    -> addMenuSettings()
//            Static.Step.SettingsMusic   -> addSettingsMusic()
//            Static.Step.MenuFruitRecord -> addMenuFruitRecord()
//            Static.Step.BonusRecords    -> addBonusRecords()
//            Static.Step.MenuGames       -> addMenuGames()
//            Static.Step.LevelsLocked    -> addLevelsLocked()
//            Static.Step.Game            -> addGame()
//        }
//
//    }
//
//    // Add ------------------------------------------------------------------------
//
//    fun addMenuRules() {
//        addActor(mask)
//        mask.setSize(width, height)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Menu.rules)
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(1019f, 462f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_menu_rules))
//            setBounds(630f, 295f, 878f, 120f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addMenuSettings() {
//        addActor(mask)
//        mask.setSize(width, height)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Menu.settings)
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(1019f, 330f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_menu_settings))
//            setBounds(580f, 187f, 878f, 120f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addMenuGames() {
//        addAndFillActor(mask)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Menu.games)
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(1019f, 630f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_menu_games))
//            setBounds(580f, 487f, 878f, 120f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addMenuFruitRecord() {
//        addAndFillActor(mask)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Menu.fruitRecord)
//
//        enable()
//
//        addActor(nextImg)
//        nextImg.setBounds(1312f, 385f, 90f, 37f)
//        nextImg.setOnClickListener(screen.game.soundUtil) { toMenuBonus() }
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(1363f, 312f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_menu_fruit_record))
//            setBounds(316f, 340f, 878f, 120f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addRulesNext() {
//        addActor(mask)
//        mask.setSize(width, height)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Rules.next)
//
//        enable()
//
//        addActor(nextImg)
//        nextImg.setBounds(128f, 123f, 90f, 37f)
//        nextImg.setOnClickListener(screen.game.soundUtil) { toRulesBack() }
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(179f, 50f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_rules_next))
//            setBounds(362f, 50f, 434f, 114f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addSettingsMusic() {
//        backgroundImg.drawable = TextureRegionDrawable(screen.game.assetsAll.b_music)
//        addActor(backgroundImg)
//        backgroundImg.setSize(width, height)
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(752f, 568f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_settings_music))
//            setBounds(363f, 427f, 878f, 120f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addBonusRecords() {
//        addAndFillActor(mask)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Bonus.records)
//
//        enable()
//
//        addActor(nextImg)
//        nextImg.setBounds(727f, 575f, 90f, 37f)
//        nextImg.setOnClickListener(screen.game.soundUtil) { toBonusGo() }
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(778f,   502f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_bonus_records))
//            setBounds(396f, 682f, 900f, 60f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addLevelsLocked() {
//        addAndFillActor(mask)
//
//        mask.addActor(visibleAreaImg)
//        visibleAreaImg.setBounds(Levels.locked)
//
//        enable()
//
//        addActor(nextImg)
//        nextImg.setBounds(1388f, 168f, 90f, 37f)
//        nextImg.setOnClickListener(screen.game.soundUtil) { toLevelsUnlocked() }
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(1439f,   95f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_levels_locked))
//            setBounds(216f, 431f, 1172f, 357f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    fun addGame() {
//        addAndFillActor(mask)
//
//        enable()
//
//        addActor(startGameImg)
//        startGameImg.setBounds(712f, 208f, 181f, 37f)
//        startGameImg.setOnClickListener(screen.game.soundUtil) { gameStartGameBlock() }
//
//        addActor(handImg)
//        handImg.apply {
//            setPosition(752f,   117f)
//            animHand()
//        }
//
//        addActor(textLbl)
//        textLbl.apply {
//            setText(screen.game.activity.getString(R.string.tutorial_game))
//            setBounds(216f, 342f, 1172f, 357f)
//            setAlignment(Align.center)
//            wrap = true
//        }
//    }
//
//    // To ------------------------------------------------------------------------
//
//    fun toRulesBack() {
//        nextImg.animHide(0.2f)
//
//        visibleAreaImg.addAction(Actions.parallel(
//            Actions.sizeTo(175f, 173f, 0.5f),
//            Actions.moveTo(1388f, 8f, 0.5f)
//        ))
//
//        handImg.addAction(Actions.moveTo(1495f, 7f, 0.4f))
//
//        textLbl.apply {
//            animHide(0.2f) {
//                setText(screen.game.activity.getString(R.string.tutorial_rules_back))
//                setBounds(576f, 67f, 765f, 172f)
//                setAlignment(Align.center)
//                wrap = true
//                animShow(0.2f) {
//                    this@ATutorials.disable()
//                    rulesBackBlock()
//                }
//            }
//        }
//    }
//
//    fun toSettingsSound() {
//        addAction(Actions.sequence(
//            Actions.delay(0.75f),
//            Actions.run {
//                backgroundImg.drawable = TextureRegionDrawable(screen.game.assetsAll.b_sound)
//
//                handImg.addAction(Actions.moveTo(752f, 275f, 0.3f))
//
//                textLbl.apply {
//                    animHide(0.2f) {
//                        setText(screen.game.activity.getString(R.string.tutorial_settings_sound))
//                        setBounds(363f, 134f, 878f, 120f)
//                        setAlignment(Align.center)
//                        wrap = true
//                        animShow(0.3f)
//                    }
//                }
//            }
//        ))
//    }
//
//    fun toSettingsBack() {
//        addAction(Actions.sequence(
//            Actions.delay(0.75f),
//            Actions.run {
//                backgroundImg.drawable = TextureRegionDrawable(screen.game.assetsAll.b_back)
//
//                handImg.addAction(Actions.moveTo(1494f, 10f, 0.3f))
//
//                textLbl.apply {
//                    animHide(0.2f) {
//                        setText(screen.game.activity.getString(R.string.tutorial_settings_back))
//                        setBounds(577f, 70f, 765f, 172f)
//                        setAlignment(Align.center)
//                        wrap = true
//                        animShow(0.3f)
//                    }
//                }
//            }
//        ))
//    }
//
//    fun toMenuBonus() {
//        nextImg.animHide(0.2f)
//
//        visibleAreaImg.addAction(Actions.parallel(
//            Actions.sizeTo(179f, 200f, 0.5f),
//            Actions.moveTo(87f, 473f, 0.5f)
//        ))
//
//        handImg.addAction(Actions.moveTo(195f, 436f, 0.4f))
//
//        textLbl.apply {
//            animHide(0.2f) {
//                setText(screen.game.activity.getString(R.string.tutorial_menu_bonus))
//                setBounds(363f, 502f, 878f, 180f)
//                setAlignment(Align.center)
//                wrap = true
//                animShow(0.2f) {
//                    this@ATutorials.disable()
//                    menuBonusBlock()
//                }
//            }
//        }
//    }
//
//    fun toBonusGo() {
//        nextImg.animHide(0.2f)
//
//        visibleAreaImg.addAction(Actions.parallel(
//            Actions.sizeTo(997f, 633f, 0.5f),
//            Actions.moveTo(475f, 119f, 0.5f)
//        ))
//
//        handImg.addAction(Actions.moveTo(1295f, 308f, 0.4f))
//
//        textLbl.apply {
//            animHide(0.2f) {
//                setText(screen.game.activity.getString(R.string.tutorial_bonus_go))
//                setBounds(59f, 789f, 1004f, 120f)
//                setAlignment(Align.center)
//                wrap = true
//                animShow(0.2f) {
//                    this@ATutorials.disable()
//                    bonusGoBlock()
//                }
//            }
//        }
//    }
//
//    fun toLevelsUnlocked() {
//        nextImg.animHide(0.2f)
//
//        visibleAreaImg.addAction(Actions.parallel(
//            Actions.sizeTo(1030f, 131f, 0.5f),
//            Actions.moveTo(287f, 594f, 0.5f)
//        ))
//
//        handImg.addAction(Actions.moveTo(903f, 558f, 0.4f))
//
//        textLbl.apply {
//            animHide(0.2f) {
//                setText(screen.game.activity.getString(R.string.tutorial_levels_unlocked))
//                setBounds(216f, 251f, 1172f, 357f)
//                setAlignment(Align.center)
//                wrap = true
//                animShow(0.2f) {
//                    this@ATutorials.disable()
//                    levelsUnlockedBlock()
//                }
//            }
//        }
//    }
//
//    // Anim
//    private fun animHand() {
//        val scale = 0.6f
//        val time  = 0.35f
//        handImg.apply {
//            setSize(100f, 100f)
//            setOrigin(44f, 90f)
//
//            addAction(Actions.forever(Actions.sequence(
//                Actions.scaleBy(-scale, -scale, time, Interpolation.slowFast),
//                Actions.scaleBy(scale, scale, time, Interpolation.fastSlow),
//            )))
//        }
//
//    }
//
//    object Static {
//        enum class Step {
//            MenuRules,
//            RulesNext,
//            MenuSettings,
//            SettingsMusic,
//            MenuFruitRecord,
//            BonusRecords,
//            MenuGames,
//            LevelsLocked,
//            Game
//        }
//    }
//
//}