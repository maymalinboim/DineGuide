package com.example.myapplication.dal.services

import com.example.myapplication.models.Review

data class ReviewDTO(
    val imageUrl: String,
    val description: String,
) {
    companion object {
        const val IMAGE_URL_KEY = "url"
        const val DESCRIPTION_KEY = "alt"
    }

    fun fromJSON(json: Map<String, Any>): ReviewDTO {
        val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""
        val description = json[DESCRIPTION_KEY] as? String ?: ""

        return ReviewDTO(imageUrl, description)
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                IMAGE_URL_KEY to imageUrl,
                DESCRIPTION_KEY to description,
            )
        }

    fun toReviews(): List<Review> {
        return reviews
    }
}
