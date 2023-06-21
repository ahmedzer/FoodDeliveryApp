package com.example.fooddeliveryapp.Auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.Entities.User
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.viewmodel.UsersViewModel
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {

    val userVM: UsersViewModel = UsersViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val uName= findViewById<EditText>(R.id.editTextTextUserName)
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val pass = findViewById<EditText>(R.id.editTextTextPassword)
        val pass2 = findViewById<EditText>(R.id.editTextTextPassword2)
        val registerBtn = findViewById<Button>(R.id.button)
        val logBtn = findViewById<Button>(R.id.logBtn)
        registerBtn.setOnClickListener {
            if(pass.text.toString().equals(pass2.text.toString())) {
                val user = User(0,uName.text.toString(), email.text.toString(),pass.text.toString(),true)
                userVM.viewModelScope.launch {
                    insertUser(user)
                }
            }
            else {
                Toast.makeText(this,"Erreur dans la confirmation",Toast.LENGTH_SHORT).show()
            }
        }

        logBtn.setOnClickListener {
            val intnt = Intent(this,LogIn::class.java)
            startActivity(intnt)
        }
    }

    fun insertUser(user: User) {
        userVM.insertUser(user)
        userVM._insertUserStatus.observe(this) {
            if(it.isSuccess) {
                Toast.makeText(this,"Compte créé avec succès",Toast.LENGTH_SHORT).show()
                runOnUiThread{
                    val sharedPrf = this.getSharedPreferences("Auth", Context.MODE_PRIVATE)
                    val editor = sharedPrf.edit()

                    editor.putBoolean("auth",true)
                        .apply()
                    finish()
                    val intent = Intent(this,LogIn::class.java)
                    startActivity(intent)
                }
            }
            else {
                Toast.makeText(this,"Erreur dans la creation du compte, adresse email déja utilisée",Toast.LENGTH_SHORT).show()
            }
        }

    }
}