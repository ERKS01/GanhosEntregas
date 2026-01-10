package com.ganhos.app.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

fun Double.formatCurrency(): String {
    return String.format("R$ %.2f", this)
}

fun String.toDoubleOrZero(): Double {
    return try {
        // Remove símbolos de moeda e espaços
        val cleaned = this.replace("R$", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
        cleaned.toDoubleOrNull() ?: 0.0
    } catch (e: Exception) {
        0.0
    }
}

fun File.compressImage(maxWidth: Int = 300, maxHeight: Int = 300): File {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(absolutePath, options)

    var scale = 1
    while (options.outWidth / scale > maxWidth || options.outHeight / scale > maxHeight) {
        scale *= 2
    }

    options.inJustDecodeBounds = false
    options.inSampleSize = scale

    val bitmap = BitmapFactory.decodeFile(absolutePath, options)
    val outputFile = File(parent, "compressed_${System.currentTimeMillis()}.jpg")

    FileOutputStream(outputFile).use { output ->
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, output)
    }

    bitmap?.recycle()
    return outputFile
}
