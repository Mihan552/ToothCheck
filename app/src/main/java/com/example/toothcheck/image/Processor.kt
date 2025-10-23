package com.example.toothcheck.imageProcessingUtils

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.core.graphics.createBitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class Processor private constructor() {
    companion object {
        fun process(imageProxy: ImageProxy, previewView: PreviewView): Bitmap {
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
            val edges = Mat()
            Imgproc.Canny(blurred, edges, 50.0, 150.0)

            val contours = ArrayList<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(
                edges,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
            )

            val contourMat = rotated.clone()
            for (contour in contours) {
                Imgproc.drawContours(contourMat, listOf(contour), -1, Scalar(0.0, 255.0, 0.0), 2)
            }

            val bmp = createBitmap(previewView.width, previewView.height)
            Imgproc.resize(
                contourMat,
                contourMat,
                Size(previewView.width.toDouble(), previewView.height.toDouble())
            )
            Utils.matToBitmap(contourMat, bmp)

            return bmp
        }
    }
}