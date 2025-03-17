package com.example.myapplication.models


data class PopulatedReview(
    var id: String = "",
    val user: User,
    val title: String = "",
    val content: String = "",
    val timestamp: Long,
    val restaurant: Restaurant,
    val imageUri: String = ""
) {
    constructor(review: Review, user: User, restaurant: Restaurant) : this(
        review.id,
        user,
        review.title,
        review.content,
        review.timestamp,
        restaurant,
        imageUri = review.imageUri ?: ""
    )
}
