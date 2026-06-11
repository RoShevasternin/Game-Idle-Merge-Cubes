package com.lewydo.idlemergecubes.game.actors.panel.menu

import com.lewydo.idlemergecubes.game.actors.AScrollPane
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonStyles
import com.lewydo.idlemergecubes.game.actors.button.base.AButtonTexture
import com.lewydo.idlemergecubes.game.actors.layout.autoLayout.AAutoLayout
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.panel.menu.settings.ASettingsSection
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelContentMenu(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors aBottomVerticalGroup
    // ------------------------------------------------------------------------
    private val aBottomVerticalGroup = AAutoLayout(screen, direction = AAutoLayout.Direction.VERTICAL, gapMain = 47f)
    private val aResetGameBtn        = AButtonTexture(screen, AButtonStyles.Texture.MENU_RESET_GAME)
    private val aCloseBtn            = AButtonTexture(screen, AButtonStyles.Texture.MENU_CLOSE)

    // ------------------------------------------------------------------------
    // Actors aContentVerticalGroup
    // ------------------------------------------------------------------------
    private val aContentVerticalGroup = AAutoLayout(screen, direction = AAutoLayout.Direction.VERTICAL, gapMain = 47f, sizingH = AAutoLayout.Sizing.HUG)
    private val aScrollPane           = AScrollPane(aContentVerticalGroup)

    private val aLeaderboardBtn  = ALeaderboardButton(screen)
    private val aSettingsSection = ASettingsSection(screen)
    private val aRemoveAdsBtn    = ARemoveAdsButton(screen)

    // ── Snapshot для відстеження змін висоти контенту ─────────────────────────

    private var lastScrollContentH = -1f

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onHeightChanged: ((totalContentHeight: Float) -> Unit)? = null

    var blockClose     = {}
    var blockClearGrid = {}

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------

    private val itemHeight           = 276f
    private val contentBetweenMargin = 71f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBottomVerticalGroup()
        addScrollPane()
    }

    // ── act(): відстежуємо зміну scroll-контенту → повідомляємо APanelMenu ───

    override fun act(delta: Float) {
        super.act(delta)
        val scrollH = aContentVerticalGroup.height
        if (scrollH != lastScrollContentH && scrollH > 0f) {
            lastScrollContentH = scrollH
            // Загальна бажана висота = скрол + відступ + нижня група
            val totalH = scrollH + contentBetweenMargin + aBottomVerticalGroup.height
            onHeightChanged?.invoke(totalH)
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors aBottomVerticalGroup
    // ------------------------------------------------------------------------

    private fun addBottomVerticalGroup() {
        aBottomVerticalGroup.setSize(width, 600f)
        add(aBottomVerticalGroup) { bottomToBottom() }

        aBottomVerticalGroup.apply {
            addResetGameBtn()
            addCloseBtn()
        }
    }

    private fun AAutoLayout.addResetGameBtn() {
        aResetGameBtn.setSize(width, itemHeight)
        add(aResetGameBtn)

        aResetGameBtn.setOnClickListener { blockClearGrid() }
    }

    private fun AAutoLayout.addCloseBtn() {
        aCloseBtn.setSize(width, itemHeight)
        add(aCloseBtn)

        aCloseBtn.setOnClickListener { blockClose() }
    }

    // ------------------------------------------------------------------------
    // Add Actors aContentVerticalGroup
    // ------------------------------------------------------------------------

    private fun addScrollPane() {
        aScrollPane.width = width
        add(aScrollPane) {
            matchHeight()
            topToTop()
            bottomToTop(aBottomVerticalGroup, contentBetweenMargin)
        }

        setUpContentVerticalGroup()
    }

    private fun setUpContentVerticalGroup() {
        aContentVerticalGroup.width = width
        aContentVerticalGroup.apply {
            addLeaderboardBtn()
            addSettingsSection()
            addRemoveAdsBtn()
        }
    }


    private fun AAutoLayout.addLeaderboardBtn() {
        aLeaderboardBtn.setSize(width, itemHeight)
        add(aLeaderboardBtn)
    }

    private fun AAutoLayout.addSettingsSection() {
        aSettingsSection.setSize(width, itemHeight)
        add(aSettingsSection)

//        aSettingsSection.addAction(Actions.sequence(
//            Actions.delay(3f),
//            Actions.forever(Actions.sequence(
//                Actions.sizeBy(0f, 4000f, 3f),
//                Actions.sizeBy(0f, -4000f, 3f),
//            ))
//        ))
    }

    private fun AAutoLayout.addRemoveAdsBtn() {
        aRemoveAdsBtn.setSize(width, itemHeight)
        add(aRemoveAdsBtn)
    }
}