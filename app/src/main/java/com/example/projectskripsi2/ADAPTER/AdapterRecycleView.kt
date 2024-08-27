package com.example.projectskripsi2.ADAPTER

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectskripsi2.R
import com.example.projectskripsi2.RESPONE.UploadRespone
import com.example.projectskripsi2.formatToRupiah

class AdapterRecycleView(private var responses: List<UploadRespone>) : RecyclerView.Adapter<AdapterRecycleView.UploadResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadResponseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemrecyle, parent, false)
        return UploadResponseViewHolder(view)
    }

    override fun onBindViewHolder(holder: UploadResponseViewHolder, position: Int) {
        val response = responses[position]
        holder.bind(response)
    }

    override fun getItemCount(): Int = responses.size

    class UploadResponseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textproduk)
        private val priceTextView: TextView = itemView.findViewById(R.id.textharga)
        private val totalTextView: TextView = itemView.findViewById(R.id.jumlahrproduk)

        fun bind(response: UploadRespone) {
            nameTextView.text = response.name
            priceTextView.text = formatToRupiah(response.price)
            totalTextView.text = response.total.toString()
        }
    }
}
