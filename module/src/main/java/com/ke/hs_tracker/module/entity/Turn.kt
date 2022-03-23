package com.ke.hs_tracker.module.entity

sealed interface Turn {


    /**
     * 游戏初始化
     */
    data class InitialGame(
        val buildNumber: String,
        val gameType: String,
        val formatType: String,
        val scenarioID: Int,
        val player1: Pair<Int, String>,
        val player2: Pair<Int, String>,
    )

    /**
     * 卡牌初始化
     */
    data class CreateGame(
        val player1Cards: List<Int>,
        val player2Cards: List<Int>,
        val player1HeroId: String,
        val player1HeroPowerId: String,
        val player2HeroId: String,
        val player2HeroPowerId: String
    ) : Turn


    /**
     * 确定手牌回合
     */
    data class First(
        val firstPlayerName: String,
        val player1Cards: List<PlayerInitialCard>,
        val player2Cards: List<PlayerInitialCard>
    ) : Turn
}

data class PlayerInitialCard(
    val playerId: Int,
    val position: Int,
    val entityId: Int,
    //自己的卡牌才能看到id
    val cardId: String?
)