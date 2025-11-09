package com.example.cartoongenerator.data.dao

import androidx.room.*
import com.example.cartoongenerator.data.model.CartoonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartoonDao {
    @Query("SELECT * FROM cartoons ORDER BY createdAt DESC")
    fun getAllCartoons(): Flow<List<CartoonEntity>>

    @Query("SELECT * FROM cartoons WHERE id = :id")
    suspend fun getCartoonById(id: Long): CartoonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartoon(cartoon: CartoonEntity): Long

    @Delete
    suspend fun deleteCartoon(cartoon: CartoonEntity)

    @Query("DELETE FROM cartoons WHERE id = :id")
    suspend fun deleteCartoonById(id: Long)

    @Query("SELECT * FROM cartoons WHERE title LIKE '%' || :query || '%'")
    fun searchCartoons(query: String): Flow<List<CartoonEntity>>
}