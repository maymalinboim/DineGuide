package com.example.myapplication.dal.services

import com.example.myapplication.models.Review
import com.example.myapplication.models.ReviewPhoto

data class ReviewsDTO(val page: Int = 0, val photos: List<ReviewPhoto> = emptyList(), val perPage: Int = 0) {
    companion object {
        const val PAGE_KEY = "page"
        const val PER_PAGE_KEY = "per_page"
        const val PHOTOS_KEY = "photos"
    }

    fun fromJSON(json: Map<String, Any>): ReviewsDTO {
        val page = json[PAGE_KEY] as? Int ?: 0
        val photos = (json[PHOTOS_KEY] as? List<Map<String, Any>> ?: emptyList()).map { ReviewPhoto.fromJSON(it) }
        val perPage = json[PER_PAGE_KEY] as? Int ?: 10
        return ReviewsDTO(page, photos, perPage)
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                PAGE_KEY to page,
                PHOTOS_KEY to photos.map { it.json },
                PER_PAGE_KEY to perPage,
            )
        }

    fun toReviews(): List<Review> {
        val reviews = photos.map { photo ->
            Review(
                id = photo.id,
                userId = "Hmo0YfwjCTNM7NDsRfQZJ3tyCq72",
                title = "aaa",
                content = photo.alt,
                timestamp = System.currentTimeMillis(),
                restaurantId = "ZCunBk7xU24YcSzamozn"
            ).apply {
                imageUri = photo.src.original
            }
        }

        return reviews
    }
}
