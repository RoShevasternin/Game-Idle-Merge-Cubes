package com.lewydo.idlemergecubes.game.model

class LevelUpRewardModel(
    private val playerModel: PlayerModel
) {

    // =====================================================
    // FORMULA
    // Нагорода = buyPrice * множник рівня
    // Level  2: 12  * 6.0  =  72 монет  (~7 кубів)
    // Level  5: 18  * 7.5  = 135 монет  (~7 кубів)
    // Level 10: 28  * 10.0 = 280 монет  (~10 кубів)
    // Level 20: 48  * 15.0 = 720 монет  (~15 кубів)
    // Гравець відчуває ріст але не ламає баланс
    // =====================================================

    fun calculateReward(newLevel: Int): Long {
        val buyPrice   = (8 + newLevel * 2).toLong()
        val multiplier = 5.0 + newLevel * 0.5
        return (buyPrice * multiplier).toLong()
    }

    fun collect(newLevel: Int) {
        playerModel.addCoins(calculateReward(newLevel))
    }

    fun collectX2(newLevel: Int) {
        playerModel.addCoins(calculateReward(newLevel) * 2)
    }
}