package com.example.myapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey
    @SerializedName("id")
    var id: String = "",
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "imagePath")
    val imagePath: String = "",
    @ColumnInfo(name = "description")
    val description: String="",
    @ColumnInfo(name = "location")
    val location: String="",
) {
    companion object {
        const val ID_KEY = "id"
        const val NAME_KEY = "name"
        const val IMAGE_PATH_KEY = "imagePath"
        const val DESCRIPTION_KEY = "description"
        const val LOCATION_KEY = "location"

        fun fromJSON(json: Map<String, Any>): Restaurant {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val imagePath = json[IMAGE_PATH_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val location = json[LOCATION_KEY] as? String ?: ""
            return Restaurant(
                id,
                name,
                imagePath,
                description,
                location,
            )
        }

    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                NAME_KEY to name,
                IMAGE_PATH_KEY to imagePath,
                DESCRIPTION_KEY to description,
                LOCATION_KEY to location,
            )
        }
}
