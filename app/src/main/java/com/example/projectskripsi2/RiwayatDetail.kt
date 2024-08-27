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
import com.example.projectskripsi2.ADAPTER.AdapterRiwayatDetail
import com.example.projectskripsi2.RESPONE.Order
import com.google.android.material.appbar.MaterialToolbar

class RiwayatDetail : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRiwayatDetail
    private lateinit var back: MaterialToolbar

    private lateinit var Tanggal: TextView
    private lateinit var idorder: TextView
    private lateinit var username: TextView
    private lateinit var totalpayment: TextView
    private lateinit var textmetodepembayaran: TextView

    private val mainModel: MainModel by viewModels()

    var orderid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_detail)

        recyclerView = findViewById(R.id.recycleviewdetail)
        Tanggal = findViewById(R.id.date)
        idorder = findViewById(R.id.IDOrder)
        username = findViewById(R.id.namapelanggan)
        recyclerView = findViewById(R.id.recycleviewdetail)
        totalpayment = findViewById(R.id.texttotalpembayaran)
        textmetodepembayaran = findViewById(R.id.textmetodepembayaran)
        back = findViewById(R.id.topbar)

        orderid = intent.getStringExtra("orderid").toString()
        val Username = intent.getStringExtra("username")

        username.text = "Nama Pelanggan : $Username"

        adapter = AdapterRiwayatDetail(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        mainModel.getOrderDetails(orderid)

        mainModel.isSuccess.observe(this, Observer { success ->
        if(success){
            mainModel.orderDetail.observe(this, Observer { order ->
                order?.let {
                    result(it)
                }
            })
        }


        })

        back.setOnClickListener {
            val intent = Intent(this,RiwayatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun result(it: Order) {
        Tanggal.text = "Tanggal : ${it.time}"
        idorder.text = "Order ID : ${it.orderId}"
        totalpayment.text = formatToRupiah(it.totalPrice)
        textmetodepembayaran.text = it.payment?.paymentMethod

        adapter.updateJadwalList(it.items)
    }
}