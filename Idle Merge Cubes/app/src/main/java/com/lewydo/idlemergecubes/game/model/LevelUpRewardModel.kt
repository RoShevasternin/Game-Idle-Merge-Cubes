package com.lewydo.idlemergecubes.game.model

class LevelUpRewardModel(private val playerModel: PlayerModel) {

    // ------------------------------------------------------------------------
    // Formula
    // Reward = buyPrice × level multiplier
    //
    // Level  2:  12 × 6.0  =  72 coins  (~7 cubes)
    // Level  5:  18 × 7.5  = 135 coins  (~7 cubes)
    // Level 10:  28 × 10.0 = 280 coins  (~10 cubes)
    // Level 20:  48 × 15.0 = 720 coins  (~15 cubes)
    // ------------------------------------------------------------------------

    fun calculateReward(level: Int): Long {
        val buyPrice   = (8 + level * 2).toLong()
        val multiplier = 5.0 + level * 0.5
        return (buyPrice * multiplier).toLong()
    }

    fun collect(level: Int) {
        playerModel.addCoins(calculateReward(level))
    }

    fun collectX2(level: Int) {
        playerModel.addCoins(calculateReward(level) * 2)
    }
}