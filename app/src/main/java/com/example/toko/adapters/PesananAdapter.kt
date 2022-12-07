package com.example.toko.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.toko.PesananActivity
import com.example.toko.R
import com.example.toko.TambahPesananActivity
import com.example.toko.models.Pesanan
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class PesananAdapter(private var pesananList: List<Pesanan>, context: Context):
    RecyclerView.Adapter<PesananAdapter.ViewHolder>(), Filterable {

    private var filteredPesananList: MutableList<Pesanan>
    private val context: Context

    init {
        filteredPesananList = ArrayList(pesananList)
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_pesanan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredPesananList.size
    }

    fun setPesananList(pesananList: Array<Pesanan>){
        this.pesananList = pesananList.toList()
        filteredPesananList = pesananList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pesanan = filteredPesananList[position]
        holder.nama_pesanan.text = pesanan.namaPesanan
        holder.jumlah_pesanan.text =  pesanan.jumlahPesanan

        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus pesanan ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if (context is PesananActivity) pesanan.id?.let { it1 -> context.deletePesanan(it1)
                    }
                }
                .show()

        }

        holder.cvPesanan.setOnClickListener {
            val i = Intent(context, TambahPesananActivity::class.java)
            i.putExtra("id", pesanan.id)
            if(context is PesananActivity)
                context.startActivityForResult(i, PesananActivity.LAUNCH_ADD_ACTIVITY)
        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<Pesanan> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(pesananList)
                }else{
                    for (pesanan in pesananList){
                        if(pesanan.namaPesanan.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))

                        )filtered.add(pesanan)

                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults

            }

            override fun publishResults( CharSequence: CharSequence, filterResults: FilterResults) {
                filteredPesananList.clear()
                filteredPesananList.addAll(filterResults.values as List<Pesanan>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nama_pesanan: TextView
        var jumlah_pesanan: TextView
        var btnDelete: ImageButton
        var cvPesanan: CardView

        init {
            nama_pesanan = itemView.findViewById(R.id.nama_pesanan)
            jumlah_pesanan = itemView.findViewById(R.id.jumlah_pesanan)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            cvPesanan = itemView.findViewById(R.id.cv_pesanan)
        }

    }
}