package com.example.projectskripsi2.ADAPTER

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectskripsi2.R
import com.example.projectskripsi2.RESPONE.OrderItem
import com.example.projectskripsi2.formatToRupiah

class AdapterRiwayatDetail(private var list: List<OrderItem>) : RecyclerView.Adapter<AdapterRiwayatDetail.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemrecyle, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = list[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = list.size

    fun updateJadwalList(newJadwalList:  List<OrderItem>) {
        list = newJadwalList
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textproduk)
        private val priceTextView: TextView = itemView.findViewById(R.id.textharga)
        private val totalTextView: TextView = itemView.findViewById(R.id.jumlahrproduk)

        fun bind(product: OrderItem) {
            nameTextView.text = product.productName
            totalTextView.text = product.qty.toString()
            priceTextView.text = formatToRupiah(product.price)
        }
    }
}