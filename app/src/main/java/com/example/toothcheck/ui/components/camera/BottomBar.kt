package com.example.toothcheck.ui.components.camera

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.toothcheck.R
import com.example.toothcheck.imageProcessingUtils.Saver

/**
 * 🎯 НИЖНЯЯ ПАНЕЛЬ УПРАВЛЕНИЯ ДЛЯ ЭКРАНА КАМЕРЫ
 *
 * Содержит основные кнопки управления:
 * - "Сфоткать": создание снимка и сохранение
 * - "Анализ кариеса": запуск процесса анализа изображения
 *
 * Автоматически скрывает кнопку анализа когда анализ активен
 */
object BottomBar {

    /**
     * 🎬 ОСНОВНОЙ КОМПОНЕНТ НИЖНЕЙ ПАНЕЛИ
     *
     * Логика отображения кнопок:
     * - Кнопка "Сфоткать" всегда видна
     * - Кнопка "Анализ кариеса" скрывается во время анализа
     *
     * @param processedBitmap обработанное изображение для сохранения
     * @param analysisMode флаг режима анализа (true = анализ активен)
     * @param onTakePhoto колбэк создания нового снимка
     * @param onStartAnalysis колбэк запуска анализа кариеса
     * @param modifier модификаторы для кастомизации layout
     */
    @Composable
    operator fun invoke(
        processedBitmap: Bitmap?,           // 🖼️ Текущее обработанное изображение (может быть null)
        analysisMode: Boolean,              // 🔍 Флаг: true = анализ в процессе, false = можно запустить анализ
        onTakePhoto: () -> Unit,            // 📸 Колбэк: создание нового фото
        onStartAnalysis: () -> Unit,        // 🦷 Колбэк: запуск анализа кариеса
        modifier: Modifier                  // 🎨 Модификаторы для настройки отображения
    ) {
        // 🏠 Получаем контекст для доступа к ресурсам и системным функциям
        val context = LocalContext.current

        // 💾 Путь сохранения изображений из строковых ресурсов
        val imageSavepoint = context.getString(R.string.image_savepoint)

        // 📏 ГОРИЗОНТАЛЬНОЕ РАСПОЛОЖЕНИЕ КНОПОК
        Row(
            modifier = Modifier
                .fillMaxWidth()              // 📱 На всю ширину экрана
                .padding(16.dp),            // 📐 Отступы от краев
            horizontalArrangement = Arrangement.SpaceEvenly  // ↔️ Равномерное распределение кнопок
        ) {
            // 📸 КНОПКА "СФОТКАТЬ" - ВСЕГДА ВИДНА
            Button(onClick = {
                // 💾 СОХРАНЕНИЕ ТЕКУЩЕГО ИЗОБРАЖЕНИЯ (если есть)
                processedBitmap?.let { bmp ->
                    Saver.saveWarnOnFail(context, bmp, imageSavepoint)
                }
                // 📸 ВЫЗОВ КОЛБЭКА ДЛЯ СОЗДАНИЯ НОВОГО СНИМКА
                onTakePhoto()
            }) {
                Text("Сфоткать")  // 🏷️ Текст кнопки
            }


            // 🦷 КНОПКА "АНАЛИЗ КАРИЕСА" - ПОКАЗЫВАЕТСЯ ТОЛЬКО КОГДА АНАЛИЗ НЕ АКТИВЕН
            if (!analysisMode) {
                Button(onClick = onStartAnalysis) {
                    Text("Анализ кариеса")  // 🏷️ Текст кнопки анализа
                }
            }
        }
    }
}