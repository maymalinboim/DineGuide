package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.dal.room.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var navigationMenu: BottomNavigationView
    lateinit var localDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        localDb = AppDatabase.getDatabase(applicationContext)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navigationMenu = findViewById(R.id.bottom_navigation)

        if (FirebaseAuth.getInstance().currentUser != null) {
            navigationMenu.visibility = View.VISIBLE
        }
        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        val navController = getNavController()
        navigationMenu = findViewById(R.id.bottom_navigation)

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                navigationMenu.visibility = View.GONE
                navController.navigate(R.id.loginFragment)
            } else {
                navigationMenu.visibility = View.VISIBLE
                navigationMenu.selectedItemId = R.id.page_2
            }
        }
        navigationMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    navController.navigate(R.id.restaurantsFragment)
                    true
                }

                R.id.page_2 -> {
                    navController.navigate(R.id.feedFragment)
                    true
                }

                R.id.page_3 -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                R.id.page_4 -> {
//                    val action = NavGraphDirections.anyPageToMyReviews()
//                    navController.navigate(action)
                    navController.navigate(R.id.feedFragment)
                    true
                }

                else -> false
            }
        }
    }

    fun getNavController(): NavController {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHost.navController
    }
}