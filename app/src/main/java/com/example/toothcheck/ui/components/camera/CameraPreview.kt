package com.example.toothcheck.ui.components.camera

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//CameraPreview - 📷 КОМПОНЕНТ КАМЕРЫ
object CameraPreview {
    @Composable
    operator fun invoke(onBack: () -> Unit) {
        var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
        var analysisMode by remember { mutableStateOf(false) }
        // ↓↓↓ ДОБАВЬ ЭТУ ПЕРЕМЕННУЮ ↓↓↓
        var analysisText by remember { mutableStateOf("Анализ не проводился") }

        val topBarHeight = 56.dp
        val bottomBarHeight = 72.dp

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                analysisMode = analysisMode,
                onBack = onBack,
                onToggleAnalysis = { analysisMode = !analysisMode },
                modifier = Modifier
                    .height(topBarHeight)
                    .fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CameraView(
                    analysisMode = analysisMode,
                    onBitmapReady = { bmp ->
                        processedBitmap = bmp
                    },
                    onAnalysisResult = { resultText ->
                        analysisText = resultText // ← ТЕПЕРЬ ЭТА ПЕРЕМЕННАЯ СУЩЕСТВУЕТ
                    },
                    modifier = Modifier.fillMaxSize()
                )

                processedBitmap?.let { bmp ->
                    PreviewImage(bmp)

                    // ↓↓↓ ДОБАВЬ ОТОБРАЖЕНИЕ ТЕКСТА ↓↓↓
                    Text(
                        text = analysisText,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = androidx.compose.material3.MaterialTheme.shapes.medium
                            )
                            .padding(8.dp)
                    )
                }
            }

            BottomBar(
                processedBitmap = processedBitmap,
                analysisMode = analysisMode,
                onTakePhoto = { /* сохранение фото */ },
                onStartAnalysis = {
                    analysisMode = true
                    analysisText = "Запуск анализа..." // ← ОБНОВЛЯЕМ ТЕКСТ ПРИ ЗАПУСКЕ
                },
                modifier = Modifier
                    .height(bottomBarHeight)
                    .fillMaxWidth()
            )
        }
    }
}