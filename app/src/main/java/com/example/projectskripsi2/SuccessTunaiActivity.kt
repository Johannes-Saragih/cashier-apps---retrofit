package com.example.projectskripsi2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class SuccessTunaiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_tunai)

        Toast.makeText(this,"Dalam waktu 10 detik, aplikasi akan tertutup secara otomatis.",Toast.LENGTH_LONG)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, BarcodeActivity::class.java)
            startActivity(intent)
        },10000)
    }
}