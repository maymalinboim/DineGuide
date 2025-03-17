package com.example.myapplication.dal.repositories

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.myapplication.dal.room.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.models.User
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UserRepository(private val context: Context) {
    companion object {
        const val USERS_COLLECTION = "users"
        const val LOCAL_IMAGES_DIR = "UserImages"
    }

    private val db = FirebaseFirestore.getInstance()
    private val localDb = AppDatabase.getDatabase(context)

    suspend fun saveUserInDB(user: User) {
        val newUser = user.copy()
        newUser.localImageUri = null  // No need to store local image URI in Firestore

        db.collection(USERS_COLLECTION)
            .document(newUser.id)
            .set(newUser.json)
            .await()

        localDb.userDao().insertAll(newUser)
    }

    suspend fun saveUserImage(imageUri: String, userId: String) {
        val localPath = saveImageLocally(imageUri.toUri(), userId)
        localDb.userDao().updateUserProfilePic(userId, localPath)
    }

    suspend fun getUserById(userId: String): User {
        var user = localDb.userDao().getUserById(userId)

        if (user != null) {
            user.imageUri = getUserImageUri(userId)  // This will use the imageUri getter
            return user
        }

        user = getUserFromFireStore(userId)
        localDb.userDao().insertAll(user)

        return user.apply { imageUri = getUserImageUri(userId) }
    }

    private suspend fun getUserFromFireStore(userId: String): User {
        val user = db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)

        user?.id = userId
        return user!!
    }

    private fun getUserImageUri(userId: String): String {
        val profileDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), LOCAL_IMAGES_DIR)
        val imageFile = File(profileDir, "$userId.jpg")

        return if (imageFile.exists()) imageFile.absolutePath else ""
    }

    private fun saveImageLocally(imageUri: Uri, userId: String): String {
        val profileDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), LOCAL_IMAGES_DIR)
        if (!profileDir.exists()) profileDir.mkdirs()

        val imageFile = File(profileDir, "$userId.jpg")

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
}
