package com.example.toothcheck

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
import android.app.AlertDialog

class MainActivity : ComponentActivity() {

    // 🔗 CALLBACK ДЛЯ ПЕРЕДАЧИ РЕЗУЛЬТАТОВ АНАЛИЗА В COMPOSE
    private var onAnalysisResult: ((Result) -> Unit)? = null

    fun setOnAnalysisResult(callback: (Result) -> Unit) {
        onAnalysisResult = callback
    }

    // 🖼️ ЗАПУСК ГАЛЕРЕИ ДЛЯ ВЫБОРА ОДНОГО ИЗОБРАЖЕНИЯ
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

    // 🖼️ ЗАПУСК ГАЛЕРЕИ ДЛЯ ВЫБОРА НЕСКОЛЬКИХ ИЗОБРАЖЕНИЙ
    private val multipleImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val clipData = result.data?.clipData
                if (clipData != null) {
                    // Множественный выбор
                    val uris = mutableListOf<Uri>()
                    for (i in 0 until clipData.itemCount) {
                        uris.add(clipData.getItemAt(i).uri)
                    }
                    processMultipleImages(uris)
                } else {
                    // Единичный выбор (для совместимости)
                    val uri = result.data?.data
                    if (uri != null) {
                        processMultipleImages(listOf(uri))
                    }
                }
            }
        }

    // 📸 ОТКРЫТИЕ ГАЛЕРЕИ ДЛЯ ВЫБОРА ОДНОГО ФОТО
    fun openGalleryForDataset() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        galleryLauncher.launch(intent)
    }

    // 📸 ОТКРЫТИЕ ГАЛЕРЕИ ДЛЯ ВЫБОРА НЕСКОЛЬКИХ ФОТО
    fun openGalleryForMultipleImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        multipleImagesLauncher.launch(intent)
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

    // 🔍 ОБРАБОТКА НЕСКОЛЬКИХ ИЗОБРАЖЕНИЙ
    private fun processMultipleImages(uris: List<Uri>) {
        try {
            val results = mutableListOf<Pair<String, Result>>()

            for ((index, uri) in uris.withIndex()) {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val result = Preparer.analyzeBitmapForCaries(bitmap)
                    val photoName = "Фото ${index + 1}"
                    results.add(photoName to result)
                }
            }

            if (results.isNotEmpty()) {
                showMultipleResultsDialog(results)
            } else {
                Toast.makeText(this, "Не удалось загрузить изображения", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка анализа: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 📊 ПОКАЗЫВАЕМ ТАБЛИЦУ РЕЗУЛЬТАТОВ ДЛЯ НЕСКОЛЬКИХ ФОТО
    private fun showMultipleResultsDialog(results: List<Pair<String, Result>>) {
        val resultText = StringBuilder()
        resultText.append("📊 РЕЗУЛЬТАТЫ АНАЛИЗА ${results.size} ФОТО\n\n")

        // Считаем статистику
        var healthyCount = 0
        var possibleCariesCount = 0
        var cariesCount = 0

        results.forEach { (photoName, result) ->
            resultText.append("$photoName:\n")
            resultText.append("   📈 Процент: ${"%.2f".format(result.affectedAreaPercent)}%\n")
            resultText.append("   🦷 Результат: ${result.riskLevel}\n")
            resultText.append("   🔍 Областей: ${result.suspiciousAreas}\n")
            resultText.append("   --------------------\n")

            // Считаем статистику
            when {
                result.riskLevel.contains("КАРИЕСА НЕТ") -> healthyCount++
                result.riskLevel.contains("ВОЗМОЖЕН") -> possibleCariesCount++
                result.riskLevel.contains("ОБНАРУЖЕН") -> cariesCount++
            }
        }

        // Добавляем итоговую статистику
        resultText.append("\n📈 ИТОГОВАЯ СТАТИСТИКА:\n")
        resultText.append("   ✅ Здоровые зубы: $healthyCount\n")
        resultText.append("   🤔 Возможен кариес: $possibleCariesCount\n")
        resultText.append("   🦷 Обнаружен кариес: $cariesCount\n")
        resultText.append("   📊 Всего проанализировано: ${results.size} фото\n")

        AlertDialog.Builder(this)
            .setTitle("Результаты анализа")
            .setMessage(resultText.toString())
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🔧 ИНИЦИАЛИЗАЦИЯ OPENCV
        if (!OpenCVLoader.initDebug()) {
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