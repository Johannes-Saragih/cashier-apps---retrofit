package com.example.projectskripsi2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import android.os.Handler
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class BarcodeActivity : AppCompatActivity() {

    private lateinit var buttonscanmember : CardView
    private lateinit var buttonpaham: Button

    private lateinit var textadmin: TextView

    private val loadingbar : Long = 3000

    private val mainModel by viewModels<MainModel>()

    private lateinit var sharedPreferences: SharedPreferences
    private val WELCOME_DIALOG_INTERVAL = 10 * 60 * 1000L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        buttonscanmember = findViewById(R.id.member)
        textadmin = findViewById(R.id.textView4)

        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        if (shouldShowWelcomeDialog()) {
            showWelcomedialog()
        }

        Log.d("BarcodeActivity", "Email: johannessaragih2001@gmail.com")

        buttonscanmember.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ){
                startQRScanner()
            } else {
                requestPermissions.launch(android.Manifest.permission.CAMERA)
            }
        }

        textadmin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun shouldShowWelcomeDialog(): Boolean {
        val lastShownTime = sharedPreferences.getLong("dialogusing", 0L)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastShownTime) >= WELCOME_DIALOG_INTERVAL
    }

    private fun showWelcomedialog() {
        val dialoginformation = Dialog(this)
        val dialog = layoutInflater.inflate(R.layout.activity_using, null)

        buttonpaham= dialog.findViewById(R.id.btnpaham)

        buttonpaham.setOnClickListener {
            dialoginformation.dismiss()
            setWelcomeDialogShown()
        }

        dialoginformation.setContentView(dialog)
        dialoginformation.setCancelable(false)
        dialoginformation.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialoginformation.show()
    }

    private fun setWelcomeDialogShown() {
        with(sharedPreferences.edit()) {
            putLong("dialogusing", System.currentTimeMillis())
            apply()
        }
    }

    private fun startQRScanner() {
        IntentIntegrator(this).initiateScan()
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startQRScanner()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {

            val dialogBinding = layoutInflater.inflate(R.layout.splash,null)

            val myDialog = Dialog(this)

            myDialog.setContentView(dialogBinding)
            myDialog.setCancelable(false)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog.show()

            mainModel.getUsernameByUserId(result.contents)

            mainModel.isSuccess.observe(this, Observer { Success ->
                Handler().postDelayed({
                    if (Success) {
                        mainModel.username.observe(this, Observer { order ->
                            order?.let {
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    putExtra("username", it)
                                    putExtra("userid",result.contents)
                                }
                                startActivity(intent)
                                myDialog.dismiss()
                            }
                        })
                    } else {
                        Toast.makeText(this, "Get order failed", Toast.LENGTH_SHORT).show()
                        myDialog.dismiss()
                    }
                },loadingbar)
            })
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
