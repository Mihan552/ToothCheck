package com.example.toothcheck.ui.components.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.toothcheck.analysis.Result

object ResultScreen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowResult(
        result: Result,
        onBack: () -> Unit
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Результат анализа") }
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Картинка с анализом
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Обработанное изображение",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // ✅ ИСПРАВЛЕНО: ОТОБРАЖЕНИЕ ОБРАБОТАННОГО ИЗОБРАЖЕНИЯ
                            Image(
                                bitmap = result.processedBitmap.asImageBitmap(),
                                contentDescription = "Результат анализа кариеса",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Изображение с выделенными зонами кариеса",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Статистика
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Статистика анализа",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            AnalysisRow("Подозрительные зоны:", "${result.suspiciousAreas}")
                            AnalysisRow("Площадь поражения:", "%.2f%%".format(result.affectedAreaPercent))

                            // Уровень риска с цветом
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Уровень риска:")
                                Text(
                                    result.riskLevel,
                                    color = when (result.riskLevel) {
                                        "🦷 ОБНАРУЖЕН КАРИЕС" -> Color.Red
                                        "🤔 ВОЗМОЖЕН КАРИЕС" -> Color(0xFFFFA500) // Оранжевый
                                        "✅ КАРИЕСА НЕТ" -> Color.Green
                                        else -> Color.Gray
                                    }
                                )
                            }
                        }
                    }

                    // Интерпретация результатов
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Рекомендации",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                when (result.riskLevel) {
                                    "🦷 ОБНАРУЖЕН КАРИЕС" -> "🔴 Рекомендуется срочно обратиться к стоматологу для лечения кариеса"
                                    "🤔 ВОЗМОЖЕН КАРИЕС" -> "🟡 Желательно посетить стоматолога в ближайшее время для профилактического осмотра"
                                    "✅ КАРИЕСА НЕТ" -> "🟢 Продолжайте поддерживать хорошую гигиену полости рта"
                                    else -> "ℹ️ " + result.riskLevel  // fallback
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Дополнительная информация о цветах
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Обозначения цветов",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ColorLegendItem("🔴 Красный", "Продвинутый кариес (высокий риск)")
                            ColorLegendItem("🟢 Зеленый", "Контуры зубов")
                        }
                    }
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Вернуться на главную")
                    }
                }
            }
        )
    }

    @Composable
    private fun AnalysisRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }

    @Composable
    private fun ColorLegendItem(color: String, description: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(color, modifier = Modifier.width(100.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}