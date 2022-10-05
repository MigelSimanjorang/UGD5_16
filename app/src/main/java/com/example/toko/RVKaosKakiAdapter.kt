package com.example.toko

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toko.entity.KaosKaki

class RVKaosKakiAdapter(private val data: Array<KaosKaki>) : RecyclerView.Adapter<RVKaosKakiAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        // Disini kita menghubungkan layout item recycler view kita
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_kaos_kaki, parent, false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        // Karena kita sudah mendefinisikan dan menghubungkan view kita,
        // kita bisa memakai view tersebut dan melakukan set text pada view tersebut
        val currentItem = data[position]
        holder.tvNamaPegawai.text = currentItem.nama
        holder.tvDetailsPegawai.text = "${currentItem.warna}"
    }

    override fun getItemCount(): Int {
        // Disini kita memberitahu jumlah dari item pada recycler view kita.
        return data.size
    }

    // Kelas ini berguna untuk menghubungkan view view yang ada pada item di recycler view kita.
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPegawai : TextView = itemView.findViewById(R.id.tv_nama_kaos_kaki)
        val tvDetailsPegawai : TextView = itemView.findViewById(R.id.tv_details_kaos_kaki)
    }
}