package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.scenes.scene2d.Group
import com.lewydo.idlemergecubes.game.actors.panel.APanelTop
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.HAlign
import com.lewydo.idlemergecubes.game.utils.actor.VAlign
import com.lewydo.idlemergecubes.game.utils.actor.addActorAligned
import com.lewydo.idlemergecubes.game.utils.actor.animDelay
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.gdxGame

class MenuScreen: AdvancedScreen() {

    private val aPanelTop = APanelTop(this)

    override fun show() {
        setBackBackground(gdxGame.assetsLoader.BACKGROUND)
        super.show()
    }

    override fun Group.addActorsOnStageUI() {
        stageUI.root.color.a = 0f

        addAPanelTop()

        animShow {

        }
    }

    override fun animHide(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShow(blockEnd: Block) {
        //stageUI.root.children.onEach { it.clearActions() }

        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // Actors ------------------------------------------------------------------------

    private fun Group.addAPanelTop() {
        aPanelTop.setSize(2160f, 467f)
        addActorAligned(aPanelTop, HAlign.CENTER, VAlign.TOP)
    }

}