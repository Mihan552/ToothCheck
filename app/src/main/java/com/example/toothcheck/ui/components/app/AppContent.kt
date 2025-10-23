package com.example.toothcheck.ui.components.app

import androidx.compose.runtime.*
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.toothcheck.MainActivity
import com.example.toothcheck.ui.components.camera.CameraPreview
import com.example.toothcheck.analysis.Result

object AppContent {
    @Composable
    operator fun invoke() {
        val context = LocalContext.current

        // 🔄 СОСТОЯНИЯ ПРИЛОЖЕНИЯ
        var cameraEnabled by remember { mutableStateOf(false) }
        var showResults by remember { mutableStateOf(false) }
        var analysisResult by remember { mutableStateOf<Result?>(null) }

        // 🔗 СВЯЗЫВАЕМСЯ С MAINACTIVITY ДЛЯ ПОЛУЧЕНИЯ РЕЗУЛЬТАТОВ
        val mainActivity = context as? MainActivity
        LaunchedEffect(mainActivity) {
            mainActivity?.setOnAnalysisResult { result ->
                analysisResult = result
                showResults = true  // ✅ АВТОМАТИЧЕСКИ ПОКАЗЫВАЕМ РЕЗУЛЬТАТЫ
                cameraEnabled = false // Закрываем камеру если была открыта
            }
        }

        // 🎨 ТЕМА ПРИЛОЖЕНИЯ
        com.example.toothcheck.ui.theme._1Theme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                // 🎯 ВЫБОР АКТИВНОГО ЭКРАНА
                when {
                    showResults && analysisResult != null -> {
                        // 📊 ЭКРАН РЕЗУЛЬТАТОВ (для камеры и галереи)
                        com.example.toothcheck.ui.components.result.ResultScreen.ShowResult(
                            result = analysisResult!!,
                            onBack = {
                                showResults = false
                                analysisResult = null
                                cameraEnabled = false
                            }
                        )
                    }

                    cameraEnabled -> {
                        // 📷 ЭКРАН КАМЕРЫ
                        CameraPreview(onBack = {
                            cameraEnabled = false
                            showResults = false
                            analysisResult = null
                        })
                    }

                    else -> {
                        // 👋 ГЛАВНЫЙ ЭКРАН
                        com.example.toothcheck.ui.components.welcome.WelcomeScreen(
                            onStartCamera = {
                                cameraEnabled = true
                                showResults = false
                                analysisResult = null
                            },
                            onCloseApp = { (context as? ComponentActivity)?.finish() },
                            onTestDataset = {
                                // 📸 ОТКРЫВАЕМ ГАЛЕРЕЮ ДЛЯ ВЫБОРА ФОТО
                                mainActivity?.openGalleryForDataset()
                            },
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}