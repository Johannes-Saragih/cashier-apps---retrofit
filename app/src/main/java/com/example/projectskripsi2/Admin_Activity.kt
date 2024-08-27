package com.example.projectskripsi2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class Admin_Activity : AppCompatActivity() {

    private lateinit var button : CardView
    private lateinit var riwayat : CardView
    private lateinit var keluar : Button

    private val loadingbar : Long = 3000

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        button = findViewById(R.id.detectionbtn)
        riwayat = findViewById(R.id.historybtn)
        keluar = findViewById(R.id.btnkeluar)

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ){
                startQRScanner()
            } else {
                requestPermissions.launch(android.Manifest.permission.CAMERA)
            }
        }

        riwayat.setOnClickListener {
            val intent = Intent (this,RiwayatActivity::class.java)
            startActivity(intent)
        }

        keluar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startQRScanner()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startQRScanner() {
        IntentIntegrator(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null){

            val dialogBinding = layoutInflater.inflate(R.layout.splash, null)

            val myDialog = Dialog(this)

            myDialog.setContentView(dialogBinding)
            myDialog.setCancelable(false)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()

            Handler().postDelayed({
                val intent = Intent(this, ResultScanProdukActivity2::class.java).apply {
                    putExtra("resiproduk",result.contents)
                }
                startActivity(intent)
                myDialog.dismiss()
            },loadingbar)
        } else {
            Toast.makeText(this,"Detection Failed",Toast.LENGTH_SHORT)
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}