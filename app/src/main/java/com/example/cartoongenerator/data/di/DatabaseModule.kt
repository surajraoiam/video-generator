package com.example.cartoongenerator.data.di

import android.content.Context
import androidx.room.Room
import com.example.cartoongenerator.data.dao.CartoonDao
import com.example.cartoongenerator.data.db.CartoonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideCartoonDatabase(
        @ApplicationContext context: Context
    ): CartoonDatabase {
        return Room.databaseBuilder(
            context,
            CartoonDatabase::class.java,
            "cartoon_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCartoonDao(database: CartoonDatabase): CartoonDao {
        return database.cartoonDao()
    }
}