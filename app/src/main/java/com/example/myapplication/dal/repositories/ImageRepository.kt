package com.example.myapplication.dal.repositories

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.myapplication.dal.room.AppDatabase
import com.example.myapplication.models.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository(private val context: Context) {
    private val cloudinary = Cloudinary(ObjectUtils.asMap(
        "cloud_name", "df9luuzoe",
        "api_key", "132483172388964",
        "api_secret", "0hB0kKucXUaeyNYNqb1no7BUYXQ"
    ))
    private val localDb = AppDatabase.getDatabase(context)

    companion object {
        const val IMAGES_FOLDER = "images"
    }

    suspend fun uploadImage(imageUri: Uri, imageId: String) {
        val filePath = imageUri.path ?: return

        withContext(Dispatchers.IO) {
            val uploadResult = cloudinary.uploader().upload(filePath, ObjectUtils.asMap(
                "public_id", "$IMAGES_FOLDER/$imageId"
            ))

            val imageUrl = uploadResult["secure_url"] as String
            localDb.imageDao().insertAll(Image(imageId, imageUrl))
        }
    }

    suspend fun getImageRemoteUri(imageId: String): Uri {
        return withContext(Dispatchers.IO) {
            Uri.parse("https://res.cloudinary.com/df9luuzoe/image/upload/$IMAGES_FOLDER/$imageId")
        }
    }

    fun downloadAndCacheImage(uri: Uri, imageId: String): String {
        val file = Glide.with(context)
            .asFile()
            .load(uri)
            .submit()
            .get()

        localDb.imageDao().insertAll(Image(imageId, file.absolutePath))
        return file.absolutePath
    }

    fun getImageLocalUri(imageId: String): String {
        return localDb.imageDao().getImageById(imageId).value?.uri ?: ""
    }

    suspend fun getImagePathById(imageId: String): String {
        val image = localDb.imageDao().getImageById(imageId).value

        if (image != null) return image.uri

        val remoteUri = getImageRemoteUri(imageId)
        val localPath = downloadAndCacheImage(remoteUri, imageId)

        localDb.imageDao().insertAll(Image(imageId, localPath))
        return localPath
    }

    suspend fun deleteImage(imageId: String) {
        withContext(Dispatchers.IO) {
            cloudinary.uploader().destroy("$IMAGES_FOLDER/$imageId", ObjectUtils.emptyMap())
            deleteLocalImage(imageId)
        }
    }

    private fun deleteLocalImage(imageId: String) {
        val image = localDb.imageDao().getImageById(imageId).value
        image?.let {
            val file = Glide.with(context)
                .asFile()
                .load(it.uri)
                .submit()
                .get()

            if (file.exists()) {
                file.delete()
            }

            localDb.imageDao().deleteImage(imageId)
        }
    }
}