package com.example.trigen.triage.classifier

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjuryClassifier @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val MODEL_FILE = "injury_model.tflite"
        private const val INPUT_SIZE = 224
        private const val PIXEL_SIZE = 3
        private const val BYTES_PER_FLOAT = 4
        private const val BATCH_SIZE = 1
    }

    private var interpreter: Interpreter? = null

    private val labels = listOf(
        InjuryLabel.BURN,
        InjuryLabel.FRACTURE,
        InjuryLabel.LACERATION,
        InjuryLabel.BITE
    )

    init {
        setupInterpreter()
    }

    private fun setupInterpreter() {
        try {
            val model = loadModelFile()
            val options = Interpreter.Options().apply {
                numThreads = 4
            }
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(bitmap: Bitmap, rotation: Int = 0): ClassificationResult {
        val interpreter = interpreter ?: return ClassificationResult(
            label = InjuryLabel.UNKNOWN,
            confidence = 0f
        )

        val processedBitmap = processInputImage(bitmap, rotation)
        val byteBuffer = bitmapToByteBuffer(processedBitmap)

        val output = Array(BATCH_SIZE) { FloatArray(labels.size) }
        try {
            interpreter.run(byteBuffer, output)
        } catch (e: Exception) {
            android.util.Log.e("InjuryClassifier", "Interpreter run failed", e)
            return ClassificationResult(InjuryLabel.UNKNOWN, 0f)
        }

        return processOutput(output[0])
    }

    private fun processInputImage(bitmap: Bitmap, rotation: Int): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val matrix = Matrix().apply {
            postRotate(rotation.toFloat())
            postScale(INPUT_SIZE.toFloat() / size, INPUT_SIZE.toFloat() / size)
        }

        return Bitmap.createBitmap(bitmap, x, y, size, size, matrix, true)
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(
            BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE * BYTES_PER_FLOAT
        )
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in pixels) {
            // Teachable Machine / default TFLite preprocessing: (x / 127.5) - 1.0
            // This is equivalent to (x - 127.5) / 127.5
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) / 127.5f) - 1.0f) // R
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) / 127.5f) - 1.0f)  // G
            byteBuffer.putFloat(((pixel and 0xFF) / 127.5f) - 1.0f)         // B
        }
        return byteBuffer
    }

    private fun processOutput(output: FloatArray): ClassificationResult {
        // Log all outputs for debugging
        val debugString = output.indices.joinToString { i -> "${labels.getOrNull(i)?.name ?: i}: ${"%.3f".format(output[i])}" }
        android.util.Log.d("InjuryClassifier", "Predictions: $debugString")

        val maxIndex = output.indices.maxByOrNull { output[it] } ?: return ClassificationResult(
            label = InjuryLabel.UNKNOWN,
            confidence = 0f
        )
        val confidence = output[maxIndex]
        val label = if (maxIndex < labels.size) labels[maxIndex] else InjuryLabel.UNKNOWN

        return ClassificationResult(
            label = label,
            confidence = confidence
        )
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
