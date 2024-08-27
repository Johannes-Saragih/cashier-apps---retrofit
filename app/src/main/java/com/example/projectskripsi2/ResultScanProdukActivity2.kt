package com.example.projectskripsi2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.appbar.MaterialToolbar

class ResultScanProdukActivity2 : AppCompatActivity() {

    private lateinit var Texthasil : TextView
    private lateinit var namabarang : EditText
    private lateinit var hargabarang : EditText
    private lateinit var btnback : MaterialToolbar
    private lateinit var button : Button

    var HasilBarcode = ""

    private val mainModel: MainModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_scan_produk2)

        Texthasil = findViewById(R.id.hasilscan)
        namabarang = findViewById(R.id.edtnamaproduk)
        hargabarang = findViewById(R.id.edtnamaharga)
        btnback = findViewById(R.id.topbar)
        button = findViewById(R.id.btninput)

        HasilBarcode = intent.getStringExtra("resiproduk").toString()

        Texthasil.text = " Kode Barang : $HasilBarcode "

        button.setOnClickListener {

            val edtnama = namabarang.text.toString()
            val edtharga = hargabarang.text.toString()

            if (edtnama.isNotEmpty() && edtharga.isNotEmpty()){
                mainModel.uploafile(HasilBarcode,edtnama,edtharga)
                mainModel.isSuccess.observe(this, Observer { isSuccess ->
                    if (isSuccess){
                     Toast.makeText(this,"Data Berhasil Ditambahkan",Toast.LENGTH_LONG)
                        val intent = Intent(this,Admin_Activity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
            }
        else{
            Toast.makeText(this,"Lengkapin Data Barang",Toast.LENGTH_LONG)
            }
        }

        btnback.setOnClickListener {
            finish()
        }
    }
}