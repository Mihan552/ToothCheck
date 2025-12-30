package com.example.toothcheck.analysis

import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.*

/**
 * 🦷 РЕЗУЛЬТАТ АНАЛИЗА КАРИЕСА
 *
 * Содержит:
 * - Обработанное изображение с выделенными зонами
 * - Количество подозрительных областей
 * - Процент площади поражения
 * - Уровень риска кариеса
 */
data class Result(
    val processedBitmap: Bitmap,      // 🖼️ Изображение с выделенными зонами
    val suspiciousAreas: Int,         // 🔍 Количество подозрительных областей
    val affectedAreaPercent: Float,   // 📊 Процент площади поражения (0-100%)
    val riskLevel: String = "НЕ ОПРЕДЕЛЕН" // 🚨 Уровень риска кариеса
) {
    /**
     * 📝 ФОРМАТИРОВАННАЯ СТРОКА ДЛЯ ОТОБРАЖЕНИЯ
     */
    fun getAnalysisSummary(): String {
        return  "Подозрительных зон: $suspiciousAreas\n" +
                "Площадь поражения: ${"%.1f".format(affectedAreaPercent)}%\n" +
                "Уровень риска: $riskLevel"
    }
}

/**
 * 📋 ПРОФИЛЬ ПАЦИЕНТА
 *
 * Содержит историю анализов и общую статистику
 */
data class PatientProfile(
    val patientId: String = "patient_${System.currentTimeMillis()}",
    val patientName: String = "Пользователь",
    val registrationDate: String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()),
    val totalAnalyses: Int = 0,
    val averageHealthPercent: Float = 100f,
    val lastAnalysisDate: String = "Не проведено"
)