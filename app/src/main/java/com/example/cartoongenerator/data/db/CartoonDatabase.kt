package com.example.cartoongenerator.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cartoongenerator.data.dao.CartoonDao
import com.example.cartoongenerator.data.model.CartoonEntity
import com.example.cartoongenerator.data.util.Converters

@Database(
    entities = [CartoonEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CartoonDatabase : RoomDatabase() {
    abstract fun cartoonDao(): CartoonDao
}