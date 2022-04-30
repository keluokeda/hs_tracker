package com.ke.hs_tracker.module.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ke.hs_tracker.module.entity.CardClass
import com.ke.hs_tracker.module.entity.FormatType
import com.ke.hs_tracker.module.entity.GameType
import java.util.*

@Entity(tableName = "game")
data class Game(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    //并不是唯一标识
    @ColumnInfo(name = "build_number")
    val buildNumber: String = "",
    @ColumnInfo(name = "game_type")
    var gameType: GameType = GameType.Unknown,
    @ColumnInfo(name = "format_type")
    var formatType: FormatType = FormatType.Unknown,
    @ColumnInfo(name = "scenario_id")
    var scenarioID: Int = 0,
    @ColumnInfo(name = "user_name")
    var userName: String = "",
    @ColumnInfo(name = "opponent_name")
    var opponentName: String = "",
    @ColumnInfo(name = "is_user_first")
    var isUserFirst: Boolean? = null,
    @ColumnInfo(name = "user_deck_name")
    var userDeckName: String = "",
    @ColumnInfo(name = "user_deck_code")
    var userDeckCode: String = "",
    @ColumnInfo(name = "is_user_win")
    var isUserWin: Boolean? = null,
    @ColumnInfo(name = "user_hero")
    var userHero: CardClass? = null,
    @ColumnInfo(name = "opponent_class")
    var opponentHero: CardClass? = null,
    @ColumnInfo(name = "start_time")
    var startTime: Long = 0,
    @ColumnInfo(name = "end_time")
    var endTime: Long = 0
) {

//    val userName: String
//        get() = if (isUserFirst == true) player1Name else player2Name

//    var opponentName: String
//        get() = if (isUserFirst == true) player2Name else player1Name
//        set(value) {
//            if (isUserFirst == true) {
//                player2Name = value
//            } else {
//                player1Name = value
//            }
//        }
}