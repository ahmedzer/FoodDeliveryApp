package com.example.fooddeliveryapp.Auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.fooddeliveryapp.R
import org.w3c.dom.Text

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val emailTextV = findViewById<TextView>(R.id.editTextTextEmailAddress)

        val passTextV = findViewById<TextView>(R.id.editTextTextPassword)
        val connectBtn = findViewById<Button>(R.id.button)

        connectBtn.setOnClickListener {
            if(passTextV.text.toString().equals("Test") && emailTextV.text.toString().equals("test@gmail.com")) {
                val sharedPrf = this.getSharedPreferences("Auth", Context.MODE_PRIVATE)
                val editor = sharedPrf.edit()
                editor.putBoolean("auth",true)
                    .apply()
                finish()
            }
            else {
                Toast.makeText(this,"Email ou mot de passe invalide",Toast.LENGTH_SHORT).show()
            }
        }
    }
}