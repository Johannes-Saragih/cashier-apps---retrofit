package com.example.projectskripsi2.ADAPTER

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectskripsi2.R
import com.example.projectskripsi2.RESPONE.OrderToday
import com.example.projectskripsi2.RiwayatDetail
import com.example.projectskripsi2.formatToRupiah

class AdapterRiwayatAdmin (private var list: List<OrderToday>, private val context: Context): RecyclerView.Adapter<AdapterRiwayatAdmin.JadwalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemriwayat, parent, false)
        return JadwalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val jadwal = list[position]
        holder.bind(jadwal, context)
    }

    override fun getItemCount(): Int = list.size

    fun updateJadwalList(newJadwalList: List<OrderToday>) {
        list = newJadwalList
        notifyDataSetChanged()
    }

    class JadwalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idOrderTextView: TextView = itemView.findViewById(R.id.id_order2)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.tanggal)
        private val totalTextView: TextView = itemView.findViewById(R.id.total2)
        private val username: TextView = itemView.findViewById(R.id.namapengguna2)
        private val metodeTextView: TextView = itemView.findViewById(R.id.pembayaran2)
        private val detailriwayat: TextView = itemView.findViewById(R.id.detail)

        fun bind(jadwal: OrderToday, context: Context) {
            idOrderTextView.text = jadwal.orderId
            tanggalTextView.text = jadwal.time
            username.text = jadwal.username
            if (jadwal.payment != null) {
                metodeTextView.text = jadwal.payment.paymentMethod
            } else {
                metodeTextView.text = "Unknown"
            }
            totalTextView.text = formatToRupiah(jadwal.totalPrice)

            detailriwayat.setOnClickListener {
                val intent = Intent(context, RiwayatDetail::class.java).apply {
                    putExtra("orderid",jadwal.orderId)
                    putExtra("username",jadwal.username)
                }
                context.startActivity(intent)
            }
        }
    }
}