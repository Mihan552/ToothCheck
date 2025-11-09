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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toothcheck.MainActivity

/**
 * 🎯 ПРИВЕТСТВЕННЫЙ ЭКРАН ПРИЛОЖЕНИЯ TOOTHCHECK
 */
object WelcomeScreen {

    @Composable
    operator fun invoke(
        onStartCamera: () -> Unit,
        onCloseApp: () -> Unit,
        onTestDataset: () -> Unit,
        innerPadding: PaddingValues
    ) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ToothCheck", fontSize = 32.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // 📸 ОСНОВНАЯ КНОПКА ЗАПУСКА КАМЕРЫ
                Button(onClick = onStartCamera) {
                    Text("Включить камеру")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 📊 КНОПКА ТЕСТА НА 1 ФОТО
                Button(onClick = {
                    val mainActivity = context as? MainActivity
                    mainActivity?.openGalleryForDataset()
                }) {
                    Text("📷 Протестировать на 1 фото")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 📊 КНОПКА ТЕСТА НА НЕСКОЛЬКИХ ФОТО
                Button(onClick = {
                    val mainActivity = context as? MainActivity
                    mainActivity?.openGalleryForMultipleImages()
                }) {
                    Text("🧪 Протестировать на нескольких фото")
                }
            }

            // 🚪 КНОПКА ВЫХОДА
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