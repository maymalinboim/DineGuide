import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.dal.repositories.ImageRepository
import com.example.myapplication.dal.repositories.RestaurantRepository
import com.example.myapplication.dal.repositories.ReviewsRepository
import com.example.myapplication.dal.repositories.UserRepository
import com.example.myapplication.ui.authScreens.feed.FeedViewModel

class FeedViewModelFactory(
    private val isMyReviews: Boolean,
    private val reviewsRepository: ReviewsRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(isMyReviews, reviewsRepository, imageRepository, userRepository, restaurantRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
