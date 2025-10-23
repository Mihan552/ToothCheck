package com.example.toothcheck.imageProcessingUtils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast

class Saver private constructor() {
    companion object {
        fun save(context: Context, bitmap: Bitmap, savepoint: String): Boolean {
            return try {
                val filename = "toothcheck_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, savepoint)
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun saveWarnOnFail(context: Context, bitmap: Bitmap, savepoint: String) {
            val success = save(context, bitmap, savepoint)
            if (success) {
                Toast.makeText(context, "Изображение сохранено в $savepoint", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
            }
        }
    }
}