package com.lewydo.idlemergecubes.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.TIME_ANIM_SCREEN
import com.lewydo.idlemergecubes.game.utils.actor.addActorWithConstraints
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedScreen
import com.lewydo.idlemergecubes.game.utils.advanced.AdvancedStage

class TestScreen: AdvancedScreen() {

    private val imgTarget = Image(drawerUtil.getRegion(Color.GREEN))

    override fun show() {
        setUIBackground(drawerUtil.getRegion(Color.GRAY))
        super.show()
    }

    override fun Group.addActorsOnStageUI() {
        imgTarget.setSize(200f, 200f)
        addActorWithConstraints(imgTarget) {
            startToStartOf   = this@addActorsOnStageUI
            endToEndOf       = this@addActorsOnStageUI
            topToTopOf       = this@addActorsOnStageUI
            bottomToBottomOf = this@addActorsOnStageUI
        }
    }

    override fun animHide(blockEnd: Block) {}

    override fun animShow(blockEnd: Block) {}
}