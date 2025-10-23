package com.example.toothcheck.ui.components.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 🎯 ВЕРХНЯЯ ПАНЕЛЬ УПРАВЛЕНИЯ ДЛЯ ЭКРАНА КАМЕРЫ
 *
 * Основные функции:
 * - Навигация назад (к предыдущему экрану)
 * - Умное управление кнопкой "Назад" в зависимости от режима
 * - Простой и чистый интерфейс управления
 *
 * Автоматически переключает поведение кнопки в зависимости от analysisMode
 */
object TopBar {

    /**
     * 🎬 ОСНОВНОЙ КОМПОНЕНТ ВЕРХНЕЙ ПАНЕЛИ
     *
     * Логика работы кнопки "Назад":
     * - В режиме анализа: выключает анализ (onToggleAnalysis)
     * - В обычном режиме: возвращает на предыдущий экран (onBack)
     *
     * @param analysisMode текущий режим анализа (true - анализ активен)
     * @param onBack колбэк возврата на предыдущий экран
     * @param onToggleAnalysis колбэк переключения режима анализа
     * @param modifier модификаторы для настройки расположения
     */
    @Composable
    operator fun invoke(
        analysisMode: Boolean,      // 🔍 Текущий режим работы: анализ или просмотр
        onBack: () -> Unit,         // ⬅️ Колбэк возврата на главный экран
        onToggleAnalysis: () -> Unit, // 🔄 Колбэк переключения режима анализа
        modifier: Modifier          // 🎨 Модификаторы для layout
    ) {
        // 📏 ГОРИЗОНТАЛЬНОЕ РАСПОЛОЖЕНИЕ ЭЛЕМЕНТОВ
        Row(
            modifier = Modifier
                .fillMaxWidth()     // 📱 НА ВСЮ ШИРИНУ ЭКРАНА
                .padding(16.dp),    // 📐 СТАНДАРТНЫЕ ОТСТУПЫ
            horizontalArrangement = Arrangement.Start  // ↔️ ВЫРАВНИВАНИЕ ВЛЕВО
        ) {
            // 🔘 УМНАЯ КНОПКА "НАЗАД" С ДВОЙНОЙ ЛОГИКОЙ
            Button(onClick = {
                // 🎯 ЛОГИКА ВЫБОРА ДЕЙСТВИЯ В ЗАВИСИМОСТИ ОТ РЕЖИМА
                if (analysisMode) {
                    // 🔄 ЕСЛИ АНАЛИЗ АКТИВЕН - ВЫКЛЮЧИТЬ АНАЛИЗ
                    onToggleAnalysis()
                } else {
                    // ⬅️ ЕСЛИ АНАЛИЗ ВЫКЛЮЧЕН - ВЕРНУТЬСЯ НАЗАД
                    onBack()
                }
            }) {
                // 🏷️ ТЕКСТ КНОПКИ (ВСЕГДА "НАЗАД")
                Text("Назад")
            }
        }
    }
}