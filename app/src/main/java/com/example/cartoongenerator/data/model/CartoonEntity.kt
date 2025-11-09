package com.example.cartoongenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cartoons")
data class CartoonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val filePath: String,
    val thumbnailPath: String,
    val styleType: String,
    val createdAt: Date,
    val duration: Long,
    val fileSize: Long
)