package com.example.fooddeliveryapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Adapters.RestaurantAdapter
import com.example.fooddeliveryapp.Entities.Restaurant
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val window = this.window
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        val navigationView = findViewById<BottomNavigationView>(R.id.nav_bottom)
        val navHost  = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3)

        navigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.fragmentPanier-> {
                    navHost?.findNavController()?.navigate(R.id.action_global_fragmentPanier)
                }
                R.id.fragment_restaurant_list-> {
                    navHost?.findNavController()?.navigate(R.id.action_global_fragmentRestaurantList)
                }
            }
            true
        }




    }
}