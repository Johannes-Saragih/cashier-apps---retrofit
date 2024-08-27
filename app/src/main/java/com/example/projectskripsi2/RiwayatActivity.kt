package com.example.projectskripsi2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectskripsi2.ADAPTER.AdapterRiwayatAdmin
import com.example.projectskripsi2.RESPONE.OrderToday
import com.google.android.material.appbar.MaterialToolbar
import java.text.NumberFormat
import java.util.Locale

class RiwayatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRiwayatAdmin
    private lateinit var back: MaterialToolbar
    private lateinit var totalpemasukan: TextView

    private val mainModel: MainModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        recyclerView = findViewById(R.id.recycleriwayat)
        back = findViewById(R.id.topbar)
        totalpemasukan = findViewById(R.id.totalpemasukan)

        adapter = AdapterRiwayatAdmin(emptyList(),this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        mainModel.getOrdersToday()

        mainModel.isSuccess.observe(this, Observer { success ->
            if (success){
                mainModel.ordersToday.observe(this, Observer { issuccess ->
                    issuccess?.let {
                        adapter.updateJadwalList(it)
                        updateTotalPemasukan(it)
                    }
                })
            }
        })

        back.setOnClickListener {
            val intent = Intent(this,Admin_Activity::class.java)
            startActivity(intent)
        }
    }

    private fun updateTotalPemasukan(orders: List<OrderToday>) {
        val total = orders.sumOf { it.totalPrice }
        totalpemasukan.text = "Pemasukan hari ini : ${formatToRupiah(total)}"
    }
}

fun formatToRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(amount)
}