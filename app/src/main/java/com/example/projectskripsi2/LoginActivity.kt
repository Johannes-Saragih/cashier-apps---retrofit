package com.example.projectskripsi2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.appbar.MaterialToolbar

class LoginActivity : AppCompatActivity() {

    private lateinit var btnlogin : Button
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var back : MaterialToolbar

    private val mainModel: MainModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.edtemail)
        password = findViewById(R.id.edtpassword)
        btnlogin = findViewById(R.id.btnlogin)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            finish()
        }

        btnlogin.setOnClickListener {

            val edtemail = email.text.toString()
            val edtpassword = password.text.toString()

            if (edtemail.isNotEmpty() and edtpassword.isNotEmpty()) {
                if (edtemail == "Admin" && edtpassword == "admin"){
                    mainModel.loginUser(email.text.toString(), password.text.toString())
                    mainModel.isSuccess.observe(this, Observer { isSuccess ->
                        if (isSuccess){
                            val intent = Intent(this,Admin_Activity::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else {
                    Toast.makeText(this,"Kamu bukan Admin", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this,"Lengkapin email dan password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}