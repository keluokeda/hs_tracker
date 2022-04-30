package com.ke.hs_tracker.module.di

import android.content.Context
import androidx.room.Room
import com.ke.hs_tracker.module.api.HearthStoneJsonApi
import com.ke.hs_tracker.module.data.PreferenceStorage
import com.ke.hs_tracker.module.data.PreferenceStorageImpl
import com.ke.hs_tracker.module.db.*
import com.ke.hs_tracker.module.entity.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(CardClassAdapter())
            .add(CardTypeAdapter())
            .add(RarityAdapter())
            .add(SpellSchoolAdapter())
            .add(MechanicsAdapter())
            .add(RaceAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideHearthStoneJsonApi(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): HearthStoneJsonApi {

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .baseUrl(HearthStoneJsonApi.BASE_URL)
            .build()
            .create(HearthStoneJsonApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "card.db"
        ).build()
    }

    @Provides
    fun provideCardDao(database: Database): CardDao {
        return database.cardDao()
    }

    @Provides
    fun provideGameDao(database: Database): GameDao = database.gameDao()



    @Provides
    fun provideZonePositionChangedEventDao(database: Database): ZonePositionChangedEventDao =
        database.zonePositionChangedEventDao()

    @Provides
    @Singleton
    fun providePreferenceStorage(preferenceStorageImpl: PreferenceStorageImpl): PreferenceStorage {
        return preferenceStorageImpl
    }


}