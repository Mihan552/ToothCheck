package com.example.toothcheck.ui.components.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

/**
 * 🎯 КОМПОНЕНТ ДЛЯ ОТОБРАЖЕНИЯ ПРЕДПРОСМОТРА ОБРАБОТАННЫХ ИЗОБРАЖЕНИЙ
 *
 * Основные функции:
 * - Отображение Bitmap как Image в Compose
 * - Полноэкранный показ обработанных изображений
 * - Используется для preview результатов анализа OpenCV
 *
 * Простой статичный компонент без состояния
 */
object PreviewImage {

    /**
     * 🎬 ОСНОВНОЙ КОМПОНЕНТ ОТОБРАЖЕНИЯ ИЗОБРАЖЕНИЯ
     *
     * Особенности отображения:
     * - Заполнение всей доступной области
     * - Масштабирование с сохранением пропорций
     * - Быстрое преобразование Bitmap в ImageBitmap
     *
     * @param bitmap обработанное изображение для отображения
     *               (результат работы ImageProcessor или ImagePreparer)
     */
    @Composable
    operator fun invoke(bitmap: Bitmap) {
        // 🖼️ КОМПОНЕНТ ИЗОБРАЖЕНИЯ JETPACK COMPOSE
        Image(
            // 🔄 ПРЕОБРАЗОВАНИЕ BITMAP В IMAGEBITMAP ДЛЯ COMPOSE
            bitmap = bitmap.asImageBitmap(),

            // 📝 ОПИСАНИЕ ДОСТУПНОСТИ (null - декоративное изображение)
            contentDescription = null,

            // 📐 МОДИФИКАТОРЫ ДЛЯ РАЗМЕЩЕНИЯ И РАЗМЕРА
            modifier = Modifier.fillMaxSize(), // 📱 ЗАНИМАЕТ ВСЮ ДОСТУПНУЮ ОБЛАСТЬ

            // 🔍 НАСТРОЙКА МАСШТАБИРОВАНИЯ ИЗОБРАЖЕНИЯ
            contentScale = ContentScale.FillBounds // ⚖️ ЗАПОЛНЕНИЕ ГРАНИЦ С ОБРЕЗКОЙ
        )
    }
}