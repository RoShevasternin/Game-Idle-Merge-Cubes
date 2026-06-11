package com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.state

import com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.APanelCollectMergeBonus
import com.lewydo.idlemergecubes.game.actors.panel.mergeBonus.APanelMergeBonus
import com.lewydo.idlemergecubes.game.utils.Block
import com.lewydo.idlemergecubes.game.utils.actor.animHide
import com.lewydo.idlemergecubes.game.utils.actor.animHideAndDisable
import com.lewydo.idlemergecubes.game.utils.actor.animShow
import com.lewydo.idlemergecubes.game.utils.actor.animShowAndEnable
import com.lewydo.idlemergecubes.game.utils.stateMachine.State
import com.lewydo.idlemergecubes.game.utils.stateMachine.StateMachine

class ReadyState(
    override val stateMachine  : StateMachine,
    val aPanelCollectMergeBonus: APanelCollectMergeBonus,
    val onEnterBlock           : Block,
): State() {

    override fun onEnter() {
        aPanelCollectMergeBonus.animShowAndEnable(0.25f)
        onEnterBlock()
    }

    override fun onExit() {
        aPanelCollectMergeBonus.animHideAndDisable(0.25f)
    }
}