package com.example.toothcheck.ui.components.camera

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.toothcheck.imageProcessingUtils.Preparer
import com.example.toothcheck.imageProcessingUtils.Processor
import java.util.concurrent.Executor

object CameraView {
    @Composable
    operator fun invoke(
        analysisMode: Boolean,
        onBitmapReady: (Bitmap?) -> Unit,
        onAnalysisResult: (String) -> Unit, // ← ДОБАВЬ ЭТОТ ПАРАМЕТР
        modifier: Modifier
    ) {
        val mainExecutor = Executor { command -> Handler(Looper.getMainLooper()).post(command) }

        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                ctx as ComponentActivity,
                                cameraSelector,
                                preview
                            )
                        } catch (exc: Exception) {
                            exc.printStackTrace()
                        }
                    }, mainExecutor)
                }
            },
            modifier = modifier,
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // Отвязываем старый анализатор
                    cameraProvider.unbindAll()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalyzer.setAnalyzer(mainExecutor) { imageProxy ->
                        if (analysisMode) {
                            // ↓↓↓ ИСПОЛЬЗУЕМ УЛУЧШЕННЫЙ АНАЛИЗ ↓↓↓
                            val analysisResult = Preparer.analyzeCariesAdvanced(imageProxy)
                            onBitmapReady(analysisResult.processedBitmap)
                            // ↓↓↓ ДОБАВЬ ЭТУ СТРОКУ - ПЕРЕДАЕМ РЕЗУЛЬТАТ ↓↓↓
                            onAnalysisResult(analysisResult.getAnalysisSummary())
                        } else {
                            // Обычный режим - зеленые контуры
                            val bmp = Processor.process(imageProxy, previewView)
                            onBitmapReady(bmp)
                            onAnalysisResult("Анализ выключен")
                        }
                        imageProxy.close()
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.bindToLifecycle(
                            previewView.context as ComponentActivity,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }
                }, mainExecutor)
            }
        )
    }
}