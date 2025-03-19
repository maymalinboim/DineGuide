package com.example.myapplication.models

import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.Date

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(name = "userId")
    val userId: String = "",
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "content")
    val content: String = "",
    @ColumnInfo(name = "timestamp")
    var timestamp: Long,
    @ColumnInfo(name = "restaurantId")
    val restaurantId: String = ""
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

    constructor() : this("", "", "", "", 0, "")

    companion object {
        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val TITLE_KEY = "title"
        const val CONTENT_KEY = "content"
        const val IMAGE_URI_KEY = "imageUri"
        const val TIMESTAMP_KEY = "timestamp"
        const val RESTAURANT_ID_KEY = "restaurantId"
    }

    fun fromJSON(json: Map<String, Any>): Review {
        val id = json[ID_KEY] as? String ?: ""
        val userId = json[USER_ID_KEY] as? String ?: ""
        val title = json[TITLE_KEY] as? String ?: ""
        val content = json[CONTENT_KEY] as? String ?: ""
        val imageUri = json[IMAGE_URI_KEY] as? String
        val timestamp = json[TIMESTAMP_KEY] as? Long ?: System.currentTimeMillis()
        val restaurantId = json[RESTAURANT_ID_KEY] as? String ?: ""
        return Review(id, userId, title, content, timestamp, restaurantId).apply {
            this.imageUri = imageUri
        }
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USER_ID_KEY to userId,
                TITLE_KEY to title,
                CONTENT_KEY to content,
                IMAGE_URI_KEY to imageUri,
                TIMESTAMP_KEY to timestamp,
                RESTAURANT_ID_KEY to restaurantId
            )
        }
}