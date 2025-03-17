package com.example.myapplication.dal.repositories

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.models.Image
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageRepository(private val context: Context) {
    private val localDb = AppDatabase.getDatabase(context)

    companion object {
        const val LOCAL_IMAGES_DIR = "UserImages"
    }

    // Upload an image and save it locally
    fun uploadImage(imageUri: Uri, imageId: String) {
        val localPath = saveImageLocally(imageUri, imageId)
        localDb.imageDao().insertAll(Image(imageId, localPath))
    }

    // Get the local path of an image using its imageId
    fun getImageLocalUri(imageId: String): String {
        return localDb.imageDao().getImageById(imageId).value?.uri ?: ""
    }

    // Download and cache the image locally
    fun downloadAndCacheImage(uri: Uri, imageId: String): String {
        val file = Glide.with(context)
            .asFile()
            .load(uri)
            .submit()
            .get()

        localDb.imageDao().insertAll(Image(imageId, file.absolutePath))

        return file.absolutePath
    }

    // Get the image path from the local database by imageId
    fun getImagePathById(imageId: String): String {
        val image = localDb.imageDao().getImageById(imageId).value

        if (image != null) return image.uri

        val localPath = getLocalImagePath(imageId)
        localDb.imageDao().insertAll(Image(imageId, localPath))

        return localPath
    }

    // Delete the image from both local storage and the local database
    fun deleteImage(imageId: String) {
        deleteLocalImage(imageId)
    }

    private fun deleteLocalImage(imageId: String) {
        val image = localDb.imageDao().getImageById(imageId).value
        image?.let {
            val file = File(it.uri)
            if (file.exists()) file.delete()

            localDb.imageDao().deleteImage(imageId)
        }
    }

    // Save an image locally on the device
    private fun saveImageLocally(imageUri: Uri, imageId: String): String {
        val profileDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), LOCAL_IMAGES_DIR)
        if (!profileDir.exists()) profileDir.mkdirs()

        val imageFile = File(profileDir, "$imageId.jpg")

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(imageFile)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    // Get the local image path from the file system
    private fun getLocalImagePath(imageId: String): String {
        val profileDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), LOCAL_IMAGES_DIR)
        val imageFile = File(profileDir, "$imageId.jpg")

        return if (imageFile.exists()) imageFile.absolutePath else ""
    }
}
