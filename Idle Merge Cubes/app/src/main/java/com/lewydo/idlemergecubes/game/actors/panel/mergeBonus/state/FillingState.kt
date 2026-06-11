package com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.state

import com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.APanelProgressMergeBonus
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.stateMachine.State
import com.lewydo.idlemergecubes.game.utils.stateMachine.StateMachine

class FillingState(
    override val stateMachine: StateMachine,
    val aPanelProgressMergeBonus: APanelProgressMergeBonus,
): State() {

    override fun onEnter() {
        aPanelProgressMergeBonus.animShow(0.25f)
    }

    override fun onExit() {
        aPanelProgressMergeBonus.animHide(0.25f)
    }
}