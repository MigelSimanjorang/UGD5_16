package com.example.toko

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toko.entity.Barang
import com.example.toko.entity.Pegawai

class RVBarangAdapter(private val data: Array<Barang>) : RecyclerView.Adapter<RVBarangAdapter.viewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        // Disini kita menghubungkan layout item recycler view kita
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_barang, parent, false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        // Karena kita sudah mendefinisikan dan menghubungkan view kita,
        // Kita bisa memakai view tersebut dan melakukan set text pada view tersebut
        val currentItem = data[position]
        holder.tvNamaBarang.text = currentItem.namaSepatu
        holder.tvDetailsBarang.text = "${currentItem.stok}"
        holder.gambar.setImageResource(currentItem.gambar)
        holder.harga.text = currentItem.HargaSepatu

    }

    override fun getItemCount(): Int {
        // Disini kita memberitahu jumlah dari item pada recycler view kita.
        return data.size
    }

    // Kelas ini berguna untuk menghubungkan view view yang ada pada item di recycler view kita.
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaBarang : TextView = itemView.findViewById(R.id.tv_nama_barang)
        val tvDetailsBarang : TextView = itemView.findViewById(R.id.tv_details_barang)
        val gambar:ImageView = itemView.findViewById(R.id.tv_gambar_sepatu)
        val harga:TextView = itemView.findViewById(R.id.tv_harga_barang)
    }
}