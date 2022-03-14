package com.ke.hs_tracker.core.api

import com.ke.hs_tracker.core.entity.Card
import retrofit2.http.GET
import retrofit2.http.Path

interface HearthStoneJsonApi {


    /**
     * 获取卡牌数据
     */
    @GET("v1/{versionCode}/{region}/cards.json")
    suspend fun getCardJsonList(
        @Path("versionCode") versionCode: String,
        @Path("region") region: String,
    ): List<Card>


    companion object {
        const val BASE_URL = "https://api.hearthstonejson.com/"
    }
}