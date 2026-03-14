package com.lewydo.idlemergecubes.game.actors.panelMenu

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.lewydo.idlemergecubes.game.actors.AScrollPane
import com.lewydo.idlemergecubes.game.actors.button.AButton
import com.lewydo.idlemergecubes.game.actors.layout.constraintLayout.AConstraintLayout
import com.lewydo.idlemergecubes.game.actors.layout.linear.AVerticalGroup
import com.lewydo.idlemergecubes.game.actors.panelMenu.settings.ASettingsSection
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen

class APanelContentMenu(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors aBottomVerticalGroup
    // ------------------------------------------------------------------------
    private val aBottomVerticalGroup = AVerticalGroup(screen, gap = 47f)
    private val aResetGameBtn        = AButton(screen, AButton.Type.MENU_RESET_GAME)
    private val aCloseBtn            = AButton(screen, AButton.Type.MENU_CLOSE)

    // ------------------------------------------------------------------------
    // Actors aContentVerticalGroup
    // ------------------------------------------------------------------------
    private val aContentVerticalGroup = AVerticalGroup(screen, gap = 47f, wrap = true)
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

    private fun AVerticalGroup.addResetGameBtn() {
        aResetGameBtn.setSize(width, itemHeight)
        addActor(aResetGameBtn)
    }

    private fun AVerticalGroup.addCloseBtn() {
        aCloseBtn.setSize(width, itemHeight)
        addActor(aCloseBtn)
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


    private fun AVerticalGroup.addLeaderboardBtn() {
        aLeaderboardBtn.setSize(width, itemHeight)
        addActor(aLeaderboardBtn)
    }

    private fun AVerticalGroup.addSettingsSection() {
        aSettingsSection.setSize(width, itemHeight)
        addActor(aSettingsSection)

        aSettingsSection.addAction(
            Actions.sequence(
            Actions.delay(3f),
            Actions.forever(Actions.sequence(
                Actions.sizeBy(0f, 4000f, 6f),
                Actions.sizeBy(0f, -4000f, 6f),
            ))
        ))
    }

    private fun AVerticalGroup.addRemoveAdsBtn() {
        aRemoveAdsBtn.setSize(width, itemHeight)
        addActor(aRemoveAdsBtn)
    }
}