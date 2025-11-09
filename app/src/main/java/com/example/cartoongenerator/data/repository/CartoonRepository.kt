    // Share cartoon by file (for images or videos)
    fun shareCartoonByFile(file: File): Intent {
        return fileStorageManager.createShareIntent(fileStorageManager.getShareableUri(file))
    }
package com.example.cartoongenerator.data.repository

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.example.cartoongenerator.data.dao.CartoonDao
import com.example.cartoongenerator.data.model.CartoonEntity
import com.example.cartoongenerator.storage.FileStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartoonRepository @Inject constructor(
    private val cartoonDao: CartoonDao,
    private val fileStorageManager: FileStorageManager
) {
    val allCartoons: Flow<List<CartoonEntity>> = cartoonDao.getAllCartoons()

    suspend fun saveCartoon(
        title: String,
        videoFile: File,
        thumbnailFile: File,
        styleType: String,
        duration: Long
    ): Long = withContext(Dispatchers.IO) {
        val cartoon = CartoonEntity(
            title = title,
            filePath = videoFile.absolutePath,
            thumbnailPath = thumbnailFile.absolutePath,
            styleType = styleType,
            createdAt = Date(),
            duration = duration,
            fileSize = videoFile.length()
        )
        cartoonDao.insertCartoon(cartoon)
    }

    suspend fun deleteCartoon(id: Long) = withContext(Dispatchers.IO) {
        val cartoon = cartoonDao.getCartoonById(id)
        cartoon?.let {
            // Delete files
            File(it.filePath).delete()
            File(it.thumbnailPath).delete()
            // Delete from database
            cartoonDao.deleteCartoon(it)
        }
    }

    suspend fun shareCartoon(id: Long) = withContext(Dispatchers.IO) {
        val cartoon = cartoonDao.getCartoonById(id) ?: return@withContext null
        val file = File(cartoon.filePath)
        if (!file.exists()) return@withContext null
        return@withContext fileStorageManager.createShareIntent(fileStorageManager.getShareableUri(file))
    }

    fun getCartoonFile(id: Long): Flow<CartoonEntity?> {
        return cartoonDao.getCartoonById(id)
    }

    suspend fun searchCartoons(query: String): Flow<List<CartoonEntity>> {
        return cartoonDao.searchCartoons(query)
    }

    suspend fun saveCartoonImage(bitmap: Bitmap, title: String) = withContext(Dispatchers.IO) {
        val file = fileStorageManager.saveCartoonImage(bitmap)
        val cartoon = CartoonEntity(
            title = title,
            filePath = file.absolutePath,
            thumbnailPath = file.absolutePath, // For images, same as filePath
            styleType = "image",
            createdAt = Date(),
            duration = 0,
            fileSize = file.length()
        )
        cartoonDao.insertCartoon(cartoon)
    }
}