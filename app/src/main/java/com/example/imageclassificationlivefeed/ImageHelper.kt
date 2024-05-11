package com.example.imageclassificationlivefeed


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.soundcloud.android.crop.Crop
import java.io.File
import java.io.FileOutputStream

class ImageHelper(private val context: Context) {

    fun selectAndCropImage(imageView: ImageView, tempFile: File) {
        val currentImage = imageView.drawable as BitmapDrawable
        val currentBitmap = currentImage.bitmap

        tempFile.createNewFile()
        val fileOutputStream = FileOutputStream(tempFile)
        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.close()

        val tempUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
        Crop.of(tempUri, tempUri)
            .withAspect(3,4)
            .start(context as MainActivity)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MainActivity.REQUEST_CODE_PICK_IMAGE && resultCode == MainActivity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                Crop.of(uri, Uri.fromFile(File(context.cacheDir, "cropped")))
                    .asSquare()
                    .start(context as MainActivity)
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == MainActivity.RESULT_OK) {
            val croppedUri = Crop.getOutput(data)
            if (croppedUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, croppedUri)
                (context as MainActivity).initializeBitmap(bitmap)
                context.handleImageBitmap(bitmap)
            }
        }
    }

    fun saveImageToGallery(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }
        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    // Display a toast indicating the image has been saved
//                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                } else {
                    // Display a toast if there was an issue saving the image
//                    Toast.makeText(context, "Failed to save image to gallery", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
