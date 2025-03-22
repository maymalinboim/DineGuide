package com.example.myapplication.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ImageSource(
    val original: String,
    val medium: String,
    val small: String
)


data class ReviewPhoto(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo(name = "url")
    val url: String = "",
    @ColumnInfo(name = "photographer")
    val photographer: String = "",
    @ColumnInfo(name = "src")
    val src: ImageSource = ImageSource(
        original = "",
        medium = "",
        small = ""
    ),
    @ColumnInfo(name = "alt")
    val alt: String = "",
) {

    companion object {
        fun fromJSON(it: Map<String, Any>): ReviewPhoto {
            val id = it[ID_KEY] as? String ?: ""
            val url = it[URL_KEY] as? String ?: ""
            val photographer = it[PHOTOGRAPHER_KEY] as? String ?: ""
            val src = it[SRC_KEY] as? ImageSource ?: ImageSource(
                original = "",
                medium = "",
                small = ""
            )
            val alt = it[ALT_KEY] as? String ?: ""
            return ReviewPhoto(id, url, photographer, src, alt);
        }

        const val ID_KEY = "id"
        const val URL_KEY = "url"
        const val PHOTOGRAPHER_KEY = "photographer"
        const val SRC_KEY = "content"
        const val ALT_KEY = "alt"
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                URL_KEY to url,
                PHOTOGRAPHER_KEY to photographer,
                SRC_KEY to src,
                ALT_KEY to alt,
                )
        }
}