package com.example.toothcheck.ui.components.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
        onOpenProfile: () -> Unit,
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
                Text(
                    text = "🦷 ToothCheck",
                    fontSize = 36.sp,
                    style = MaterialTheme.typography.displaySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Анализ кариеса по фото",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 👤 КНОПКА ПРОФИЛЯ ПАЦИЕНТА (ОСНОВНАЯ)
                Button(
                    onClick = onOpenProfile,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("👤 Профиль", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 📸 КНОПКА КАМЕРЫ
                Button(
                    onClick = onStartCamera,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("📷 Включить камеру", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 📁 КНОПКА ГАЛЕРЕИ (1 фото)
                Button(
                    onClick = {
                        val mainActivity = context as? MainActivity
                        mainActivity?.openGalleryForDataset()
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("📁 Анализ 1 фото из галереи", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 📁 КНОПКА ГАЛЕРЕИ (несколько фото)
                Button(
                    onClick = {
                        val mainActivity = context as? MainActivity
                        mainActivity?.openGalleryForMultipleImages()
                    },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("📂 Анализ нескольких фото", fontSize = 16.sp)
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