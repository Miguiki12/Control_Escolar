package com.example.control_escolar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream

object PictureLoader {
    fun loadPicture(context: Context, byteArray: ByteArray?): Bitmap {
        val defaultBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.alumno)
        byteArray?.let {
            if (it.isNotEmpty()) {
                val options = BitmapFactory.Options().apply {
                    inBitmap = defaultBitmap
                }
                return BitmapFactory.decodeByteArray(it, 0, it.size, options)
            }
        }
        return defaultBitmap
    }
    fun loadPicture(context: Context, byteArray: ByteArray?, imageView: ImageView) {
        val defaultBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.alumno)
        byteArray?.let {
            if (it.isNotEmpty()) {
                val options = BitmapFactory.Options().apply {
                    inBitmap = defaultBitmap
                }
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size, options)
                Glide.with(context)
                    .load(bitmap)
                    .into(imageView)
            } else {
                Glide.with(context)
                    .load(defaultBitmap)
                    .into(imageView)
            }
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}