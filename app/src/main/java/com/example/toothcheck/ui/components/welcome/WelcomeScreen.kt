package com.example.toothcheck.ui.components.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🎯 ПРИВЕТСТВЕННЫЙ ЭКРАН ПРИЛОЖЕНИЯ TOOTHCHECK
 *
 * Основные функции:
 * - Представление приложения и его назначения
 * - Запуск основного функционала (камера для анализа)
 * - Выход из приложения
 * - Центральный узел навигации приложения
 *
 * Первый экран, который видит пользователь при запуске
 */
//👋 ПРИВЕТСТВЕННЫЙ ЭКРАН WelcomeScreen
object WelcomeScreen {

    /**
     * 🎬 ОСНОВНОЙ КОМПОНЕНТ ПРИВЕТСТВЕННОГО ЭКРАНА
     *
     * Компоновка экрана:
     * - Центральный блок: название приложения и кнопка запуска
     * - Угловая кнопка: выход из приложения
     * - Автоматические отступы под системные панели
     *
     * @param onStartCamera колбэк запуска экрана камеры для анализа
     * @param onCloseApp колбэк закрытия приложения
     * @param onTestDataset колбэк тестирования на датасете
     * @param innerPadding автоматические отступы от системных панелей
     */
    @Composable
    operator fun invoke(
        onStartCamera: () -> Unit,      // 📸 Колбэк запуска камеры для анализа зубов
        onCloseApp: () -> Unit,         // 🚪 Колбэк выхода из приложения
        onTestDataset: () -> Unit,      // 📊 Колбэк тестирования на датасете
        innerPadding: PaddingValues     // 📐 Автоматические отступы под системные панели
    ) {
        // 📦 ОСНОВНОЙ КОНТЕЙНЕР ВСЕГО ЭКРАНА
        Box(
            modifier = Modifier
                .fillMaxSize()          // 📱 ЗАНИМАЕТ ВЕСЬ ЭКРАН
                .padding(innerPadding)  // 📐 УЧЕТ СИСТЕМНЫХ ПАНЕЛЕЙ (notch, status bar)
        ) {
            // 🎯 ЦЕНТРАЛЬНЫЙ БЛОК С КОНТЕНТОМ
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🏷️ НАЗВАНИЕ ПРИЛОЖЕНИЯ
                Text(text = "ToothCheck", fontSize = 32.sp)

                // 📏 ПРОБЕЛ МЕЖДУ ЭЛЕМЕНТАМИ
                Spacer(modifier = Modifier.height(16.dp))

                // 📸 ОСНОВНАЯ КНОПКА ЗАПУСКА КАМЕРЫ
                Button(onClick = onStartCamera) {
                    Text("Включить камеру")
                }

                // 📏 ПРОБЕЛ МЕЖДУ КНОПКАМИ
                Spacer(modifier = Modifier.height(8.dp))

                // 📊 КНОПКА ТЕСТА ДАТАСЕТА
                Button(onClick = onTestDataset) {
                    Text("Протестировать на датасете")
                }
            }

            // 🚪 КНОПКА ВЫХОДА ИЗ ПРИЛОЖЕНИЯ (В ПРАВОМ ВЕРХНЕМ УГЛУ)
            Button(
                onClick = onCloseApp,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("❌", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}