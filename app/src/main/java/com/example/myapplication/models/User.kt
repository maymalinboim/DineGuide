package com.example.myapplication.models

import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(name = "email")
    val email: String? = "",
    @ColumnInfo(name = "first_name")
    val firstName: String? = "",
    @ColumnInfo(name = "last_name")
    val lastName: String? = ""
) {

    var remoteImageUri: String? = ""
    @ColumnInfo(name = "image_uri")
    var localImageUri: String? = ""

    var imageUri: String?
        get() = localImageUri ?: remoteImageUri
        set(value) {
            val uri = value?.toUri()
            if (uri != null && (uri.scheme == "http" || uri.scheme == "https")) remoteImageUri =
                value
            else localImageUri = value
        }

    companion object {
        const val EMAIL_KEY = "email"
        const val FIRST_NAME_KEY = "firstName"
        const val LAST_NAME_KEY = "lastName"
        const val IMAGE_URI_KEY = "imageUri"
    }

    fun fromJSON(json: Map<String, Any>): User {
        val email = json[EMAIL_KEY] as? String ?: ""
        val firstName = json[FIRST_NAME_KEY] as? String ?: ""
        val lastName = json[LAST_NAME_KEY] as? String ?: ""
        val imageUri = json[IMAGE_URI_KEY] as? String
        return User(email, firstName, lastName, imageUri)
    }

    val json: HashMap<String, String?>
        get() {
            return hashMapOf(
                EMAIL_KEY to email,
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                IMAGE_URI_KEY to imageUri
            )
        }
}
