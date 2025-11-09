package com.example.cartoongenerator.storage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileStorageManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val FILE_PROVIDER_AUTHORITY = "com.example.cartoongenerator.fileprovider"
        private const val CARTOON_FILE_PREFIX = "cartoon_"
        private const val CARTOON_FILE_EXTENSION = ".jpg"
    }

    private val cartoonDirectory: File
        get() = File(context.filesDir, "cartoons").apply { 
            if (!exists()) mkdirs() 
        }

    fun saveCartoonImage(bitmap: Bitmap): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = CARTOON_FILE_PREFIX + timeStamp + CARTOON_FILE_EXTENSION
        val file = File(cartoonDirectory, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        return file
    }

    fun getShareableUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            FILE_PROVIDER_AUTHORITY,
            file
        )
    }

    fun createShareIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun deleteCartoon(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }

    fun getCartoonFiles(): List<File> {
        return cartoonDirectory.listFiles()
            ?.filter { it.name.startsWith(CARTOON_FILE_PREFIX) }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }
}