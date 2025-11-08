package com.example.toothcheck.imageProcessingUtils

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.core.graphics.createBitmap
import com.example.toothcheck.analysis.Result
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfInt
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class Preparer private constructor() {
    companion object {
        // ↓↓↓ ОБЩИЙ МЕТОД ДЛЯ АНАЛИЗА КАРИЕСА ↓↓↓
        private fun analyzeCariesCommon(inputMat: Mat): Result {
            // 1. Конвертируем в HSV для лучшего анализа цвета
            val hsvMat = Mat()
            Imgproc.cvtColor(inputMat, hsvMat, Imgproc.COLOR_BGR2HSV)

            // 2. ОДНА УНИВЕРСАЛЬНАЯ МАСКА ДЛЯ ВСЕХ СТАДИЙ КАРИЕСА
            val lowerCaries = Scalar(0.0, 80.0, 30.0)    // БОЛЕЕ РАЗБОРЧИВО
            val upperCaries = Scalar(180.0, 200.0, 80.0) // БОЛЕЕ РАЗБОРЧИВО
            // 3. Создаем единую маску кариеса
            val cariesMask = Mat()
            Core.inRange(hsvMat, lowerCaries, upperCaries, cariesMask)

            // 4. ОБНАРУЖЕНИЕ КОНТУРОВ ЗУБОВ
            val teethContourMask = detectTeethContours(inputMat)

            // 5. ПРИМЕНЯЕМ МАСКУ ЗУБОВ К МАСКЕ КАРИЕСА
            val cariesOnTeethOnly = Mat()
            Core.bitwise_and(cariesMask, teethContourMask, cariesOnTeethOnly)

            // 6. ПРОСТОЙ ПРОЦЕНТ КАРИЕСА ОТ ВСЕГО ЗУБА
            val totalTeethPixels = Core.countNonZero(teethContourMask)
            val cariesPixels = Core.countNonZero(cariesOnTeethOnly)
            val darkSpotsPercent = if (totalTeethPixels > 0) {
                (cariesPixels.toFloat() / totalTeethPixels.toFloat()) * 100f
            } else {
                0f
            }

            // ДОБАВЬ ОТЛАДКУ
            println("🦷 ДЕБАГ: Всего зуб: $totalTeethPixels, Кариес: $cariesPixels, Процент: $darkSpotsPercent%")

            // 7. Определяем уровень риска ПО ТЕМНЫМ ВКРАПЛЕНИЯМ
            val riskLevel = when {
                darkSpotsPercent > 1.2 -> "🦷 ОБНАРУЖЕН КАРИЕС"    // Есть кариес
                darkSpotsPercent > 0.5 -> "🤔 ВОЗМОЖЕН КАРИЕС"     // Сомнительный случай
                else -> "✅ КАРИЕСА НЕТ"                          // Здоровый
            }

            // 8. ПОДСВЕЧИВАЕМ РЕЗУЛЬТАТ - КАК БЫЛО
            val resultMat = inputMat.clone()

            // 🔴 КРАСНЫЙ - КАРИЕС (BGR: 255,0,0)
            resultMat.setTo(Scalar(255.0, 0.0, 0.0), cariesOnTeethOnly)

            // 🟢 ЗЕЛЕНАЯ ОБВОДКА ВОКРУГ ЗУБОВ (BGR: 0,255,0)
            val teethContours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(teethContourMask, teethContours, hierarchy,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

            for (contour in teethContours) {
                Imgproc.drawContours(resultMat, listOf(contour), -1, Scalar(0.0, 255.0, 0.0), 3)
            }

            // Конвертируем обратно в Bitmap
            val resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(resultMat, resultBitmap)

            return Result(
                processedBitmap = resultBitmap,
                suspiciousAreas = Core.countNonZero(cariesOnTeethOnly), // Количество красных пикселей
                affectedAreaPercent = darkSpotsPercent, // 🔴 ТЕПЕРЬ ЭТО ПРОЦЕНТ ТЕМНОГО ВНУТРИ КРАСНОГО
                riskLevel = riskLevel
            )
        }

        // 🔴 МЕТОД: Подсчет процента темных вкраплений внутри красного
        private fun calculateDarkSpotsInRed(cariesMask: Mat, originalMat: Mat): Float {
            if (Core.countNonZero(cariesMask) == 0) return 0f

            // 1. Берем оригинальное изображение только в области кариеса
            val roiOriginal = Mat()
            originalMat.copyTo(roiOriginal, cariesMask)

            // 2. Конвертируем в grayscale
            val roiGray = Mat()
            Imgproc.cvtColor(roiOriginal, roiGray, Imgproc.COLOR_BGR2GRAY)

            // 3. 🔴 ИЩЕМ ТЕМНЫЕ ПИКСЕЛИ (кариес) - ИНВЕРТИРУЕМ ЛОГИКУ!
            val darkMask = Mat()
            Imgproc.threshold(roiGray, darkMask, 150.0, 255.0, Imgproc.THRESH_BINARY_INV)

            // 4. Берем темные пиксели ТОЛЬКО внутри кариеса
            val darkSpotsInCaries = Mat()
            Core.bitwise_and(cariesMask, darkMask, darkSpotsInCaries)

            // 5. Считаем процент темных пикселей ОТ ПЛОЩАДИ КАРИЕСА
            val totalCariesPixels = Core.countNonZero(cariesMask)
            val darkPixels = Core.countNonZero(darkSpotsInCaries)

            if (totalCariesPixels == 0) return 0f

            // 🔴 ДОБАВИМ ПРОВЕРКУ ДЛЯ ОТЛАДКИ
            println("🔴 ДЕБАГ: Всего кариеса: $totalCariesPixels, Темных пятен: $darkPixels")

            // Процент темного ОТ ПЛОЩАДИ КРАСНОГО (кариеса)
            val percent = (darkPixels.toFloat() / totalCariesPixels.toFloat()) * 100f
            println("🔴 ДЕБАГ: Процент темных пятен: $percent%")
            println("🔴 ДЕБАГ: ====== ДЕТАЛЬНАЯ ИНФОРМАЦИЯ =====")
            println("🔴 ДЕБАГ: Всего пикселей в маске кариеса: $totalCariesPixels")
            println("🔴 ДЕБАГ: Темных пикселей найдено: $darkPixels")
            println("🔴 ДЕБАГ: Процент: $percent%")
            println("🔴 ДЕБАГ: =================================")

            return percent
        }

        // ↓↓↓ МЕТОД ДЛЯ ОБНАРУЖЕНИЯ РЕЗКИХ ИЗМЕНЕНИЙ ЦВЕТА ↓↓↓
        private fun detectColorEdges(inputMat: Mat): Mat {
            val gray = Mat()
            Imgproc.cvtColor(inputMat, gray, Imgproc.COLOR_BGR2GRAY)

            val blurred = Mat()
            Imgproc.GaussianBlur(gray, blurred, Size(3.0, 3.0), 0.0)

            val edges = Mat()
            Imgproc.Canny(blurred, edges, 30.0, 100.0)

            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(2.0, 2.0))
            Imgproc.dilate(edges, edges, kernel)

            return edges
        }

        // ↓↓↓ МЕТОД ДЛЯ КОМПАКТНОЙ ОБВОДКИ ВОКРУГ ЗУБОВ ↓↓↓
        private fun detectTeethContours(inputMat: Mat): Mat {
            val hsvMat = Mat()
            Imgproc.cvtColor(inputMat, hsvMat, Imgproc.COLOR_BGR2HSV)

            val lowerWhite = Scalar(0.0, 0.0, 150.0)
            val upperWhite = Scalar(180.0, 50.0, 255.0)

            val teethMask = Mat()
            Core.inRange(hsvMat, lowerWhite, upperWhite, teethMask)

            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, Size(8.0, 8.0))
            Imgproc.morphologyEx(teethMask, teethMask, Imgproc.MORPH_CLOSE, kernel)
            Imgproc.morphologyEx(teethMask, teethMask, Imgproc.MORPH_OPEN, kernel)

            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(teethMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

            if (contours.isEmpty()) {
                return teethMask
            }

            val allPointsList = mutableListOf<org.opencv.core.Point>()
            for (contour in contours) {
                val area = Imgproc.contourArea(contour)
                if (area > 500.0) {
                    allPointsList.addAll(contour.toList())
                }
            }

            if (allPointsList.isEmpty()) {
                return teethMask
            }

            val allPoints = MatOfPoint()
            allPoints.fromList(allPointsList)

            val hullIndices = MatOfInt()
            Imgproc.convexHull(allPoints, hullIndices)

            val hullPointsList = mutableListOf<org.opencv.core.Point>()
            val indices = hullIndices.toArray()
            val pointsArray = allPoints.toArray()

            for (index in indices) {
                hullPointsList.add(pointsArray[index])
            }

            val hullPoints = MatOfPoint()
            hullPoints.fromList(hullPointsList)

            val convexTeethMask = Mat.zeros(teethMask.size(), teethMask.type())
            Imgproc.drawContours(convexTeethMask, listOf(hullPoints), -1, Scalar(255.0), -1)

            return convexTeethMask
        }

        // ↓↓↓ СТАРЫЙ МЕТОД ДЛЯ ПРОСТОЙ ПОДГОТОВКИ ↓↓↓
        fun prepareForAnalysis(imageProxy: ImageProxy): Bitmap {
            val bitmap = imageProxy.toBitmap()
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            val rotated = Mat()
            Core.transpose(mat, rotated)
            Core.flip(rotated, rotated, 1)

            val gray = Mat()
            Imgproc.cvtColor(rotated, gray, Imgproc.COLOR_BGR2GRAY)
            val blurred = Mat()
            Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

            val clahe = Imgproc.createCLAHE()
            clahe.clipLimit = 2.0
            val enhanced = Mat()
            clahe.apply(blurred, enhanced)

            val binary = Mat()
            Imgproc.threshold(enhanced, binary, 90.0, 255.0, Imgproc.THRESH_BINARY_INV)

            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(
                binary,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
            )

            val resultMat = rotated.clone()

            for (contour in contours) {
                Imgproc.drawContours(resultMat, listOf(contour), -1, Scalar(0.0, 0.0, 255.0), -1)
            }

            val bmp = createBitmap(resultMat.cols(), resultMat.rows())
            Utils.matToBitmap(resultMat, bmp)

            imageProxy.close()
            return bmp
        }

        // ↓↓↓ АНАЛИЗ ДЛЯ КАМЕРЫ ↓↓↓
        fun analyzeCariesAdvanced(imageProxy: ImageProxy): Result {
            val bitmap = imageProxy.toBitmap()
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            val rotated = Mat()
            Core.transpose(mat, rotated)
            Core.flip(rotated, rotated, 1)

            val result = analyzeCariesCommon(rotated)
            imageProxy.close()
            return result
        }

        // ↓↓↓ АНАЛИЗ ДЛЯ BITMAP ИЗ ГАЛЕРЕИ ↓↓↓
        fun analyzeBitmapForCaries(bitmap: Bitmap): Result {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            return analyzeCariesCommon(mat)
        }
    }
}