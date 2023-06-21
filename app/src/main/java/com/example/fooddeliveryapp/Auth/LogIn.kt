package com.example.fooddeliveryapp.Auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.fooddeliveryapp.MainActivity
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.viewmodel.UsersViewModel

class LogIn : AppCompatActivity() {

    private lateinit var userVM:UsersViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        userVM = ViewModelProvider(this).get(UsersViewModel::class.java)

        val emailTextV = findViewById<TextView>(R.id.editTextTextEmailAddress)

        val passTextV = findViewById<TextView>(R.id.editTextTextPassword)
        val connectBtn = findViewById<Button>(R.id.button)
        val registerBtn = findViewById<Button>(R.id.btnReg)
        connectBtn.setOnClickListener {
            userLogin(emailTextV.text.toString(),passTextV.text.toString())
        }

        registerBtn.setOnClickListener {
            val intnt = Intent(this,Register::class.java)
            startActivity(intnt)
        }
    }

    fun userLogin(email: String,pass:String){//call view model function to retreive user by email and password
        userVM.logUser(email,pass).observe(this){
            if(it?.uid != null) {//si le resultat n'est pas null
                if(email.equals(it.email) && pass.equals(it.password) && it.state) {//check is the user is banned or not
                    log(it.uid)
                }
            }
            else {
                runOnUiThread {
                    Toast.makeText(applicationContext,"Email ou mot de passe incorrecte!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun log(uid:Int) {//insert user info in shared prefs
        val sharedPrf = this.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = sharedPrf.edit()
        editor.putBoolean("auth",true).putInt("userId",uid)
            .apply()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}