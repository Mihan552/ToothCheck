package com.example.toothcheck

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.toothcheck.analysis.Result
import com.example.toothcheck.imageProcessingUtils.Preparer
import com.example.toothcheck.ui.components.app.AppContent
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {

    // 🔗 CALLBACK ДЛЯ ПЕРЕДАЧИ РЕЗУЛЬТАТОВ АНАЛИЗА В COMPOSE
    private var onAnalysisResult: ((Result) -> Unit)? = null

    fun setOnAnalysisResult(callback: (Result) -> Unit) {
        onAnalysisResult = callback
    }

    // 🖼️ ЗАПУСК ГАЛЕРЕИ ДЛЯ ВЫБОРА ИЗОБРАЖЕНИЯ
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        if (bitmap != null) {
                            // ✅ ЗАПУСК АНАЛИЗА ВЫБРАННОГО ИЗОБРАЖЕНИЯ
                            processSelectedImage(bitmap)
                        } else {
                            Toast.makeText(this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Ошибка загрузки фото: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    // 📸 ОТКРЫТИЕ ГАЛЕРЕИ ДЛЯ ВЫБОРА ТЕСТОВОГО ИЗОБРАЖЕНИЯ
    fun openGalleryForDataset() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        galleryLauncher.launch(intent)
    }

    // 🔍 ОБРАБОТКА ВЫБРАННОГО ИЗОБРАЖЕНИЯ И АНАЛИЗ КАРИЕСА
    private fun processSelectedImage(bitmap: android.graphics.Bitmap) {
        try {
            // ✅ ВЫЗОВ ФУНКЦИИ АНАЛИЗА ДЛЯ BITMAP ИЗ ГАЛЕРЕИ
            val result = Preparer.analyzeBitmapForCaries(bitmap)

            // ✅ УВЕДОМЛЕНИЕ О ЗАВЕРШЕНИИ АНАЛИЗА
            Toast.makeText(
                this,
                "Анализ завершен! Уровень риска: ${result.riskLevel}",
                Toast.LENGTH_LONG
            ).show()

            // ✅ ПЕРЕДАЧА РЕЗУЛЬТАТА В COMPOSE ДЛЯ ОТОБРАЖЕНИЯ
            onAnalysisResult?.invoke(result)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Ошибка анализа изображения: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🔧 ИНИЦИАЛИЗАЦИЯ OPENCV
        if (!OpenCVLoader.initLocal()) {
            Toast.makeText(this, "Не удалось загрузить OpenCV", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "OpenCV успешно загружен", Toast.LENGTH_SHORT).show()
        }

        // 🎬 ЗАПУСК COMPOSE UI
        setContent {
            AppContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppContent() {
    AppContent()
}