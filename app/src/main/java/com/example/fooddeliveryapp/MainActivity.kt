package com.example.fooddeliveryapp

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Adapters.RestaurantAdapter
import com.example.fooddeliveryapp.Entities.Restaurant
import com.example.fooddeliveryapp.viewmodel.UsersViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    public lateinit var userVM: UsersViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userVM = ViewModelProvider(this).get(UsersViewModel::class.java)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
            if(checkUserAuth()) {
                userVM.addToken(getUserId(),token.toString())
                userVM._addNewToken.observe(this){
                    if(it.isSuccess){
                        Log.d("Connection to firebase avec succès !! : ",msg)
                    }
                    else {
                        Toast.makeText(this,it.exceptionOrNull()!!.message,Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d(TAG, msg)
            }
        })

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

    fun getUserId():Int {//recuperer le user_id sauvegardé
        val sharedPrf = applicationContext.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val uid  = sharedPrf.getInt("userId",-1)
        return uid
    }

    fun checkUserAuth():Boolean {//verifier si le user est authentifié
        val sharedPrf = applicationContext.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = sharedPrf.edit()
        val auth = sharedPrf.getBoolean("auth",false)
        return auth
    }
}