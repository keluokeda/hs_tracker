package com.ke.hs_tracker.module.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ke.hs_tracker.module.entity.CardClass

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "build_number")
    val buildNumber: String = "",
    @ColumnInfo(name = "game_type")
    var gameType: String = "",
    @ColumnInfo(name = "format_type")
    var formatType: String = "",
    @ColumnInfo(name = "scenario_id")
    var scenarioID: Int = 0,
    @ColumnInfo(name = "player1_name")
    var player1Name: String = "",
    @ColumnInfo(name = "player2_name")
    var player2Name: String = "",
    @ColumnInfo(name = "is_user_first")
    var isUserFirst: Boolean? = null,
    @ColumnInfo(name = "user_deck_name")
    var userDeckName: String = "",
    @ColumnInfo(name = "user_deck_code")
    var userDeckCode: String = "",
    @ColumnInfo(name = "is_user_win")
    var isUserWin: Boolean? = null,

    var userHero: CardClass? = null,
    var opponentHero: CardClass? = null
) {

    val userName: String
        get() = if (isUserFirst == true) player1Name else player2Name

    var opponentName: String
        get() = if (isUserFirst == true) player2Name else player1Name
        set(value) {
            if (isUserFirst == true) {
                player2Name = value
            } else {
                player1Name = value
            }
        }
}