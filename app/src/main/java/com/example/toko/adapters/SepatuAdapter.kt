package com.example.toko.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.FragmentSepatu
import com.example.toko.HomeActivity
import com.example.toko.R
import com.example.toko.api.SepatuApi
import com.example.toko.models.Sepatu
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class SepatuAdapter(private var sepatuList: List<Sepatu>, context: Context):
    RecyclerView.Adapter<SepatuAdapter.ViewHolder>(), Filterable {

    private var filteredSepatuList: MutableList<Sepatu>
    private val context: Context
    private var queue: RequestQueue? = null

    init {
        filteredSepatuList = ArrayList(sepatuList)
        this.context = context
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_sepatu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredSepatuList.size
    }

    fun setSepatuList(sepatuList: Array<Sepatu>){
        this.sepatuList = sepatuList.toList()
        filteredSepatuList = sepatuList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sepatu = filteredSepatuList[position]
        holder.nama_sepatu.text = sepatu.namaSepatu
        holder.jumlah.text =  sepatu.jumlah
        holder.ukuran.text =  sepatu.ukuran
        holder.harga.text =  sepatu.harga

        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus sepatu ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if (context is HomeActivity) sepatu.id?.let { it1 -> deleteSepatu(it1)
                    }
                }
                .show()
        }

//        holder.cvSepatu.setOnClickListener {
//            val i = Intent(context, FragmentSepatu::class.java)
//            i.putExtra("id", sepatu.id)
//            if(context is FragmentSepatu)
//                context.startActivityForResult(i, HomeActivity.LAUNCH_ADD_ACTIVITY)
//        }


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<Sepatu> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(sepatuList)
                }else{
                    for (sepatu in sepatuList){
                        if(sepatu.namaSepatu.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))

                        )filtered.add(sepatu)

                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults

            }

            override fun publishResults( CharSequence: CharSequence, filterResults: FilterResults) {
                filteredSepatuList.clear()
                filteredSepatuList.addAll(filterResults.values as List<Sepatu>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nama_sepatu: TextView
        var jumlah: TextView
        var ukuran: TextView
        var harga: TextView
        var btnDelete: ImageButton
        var cvSepatu: CardView

        init {
            nama_sepatu = itemView.findViewById(R.id.nama_sepatu)
            jumlah = itemView.findViewById(R.id.jumlah)
            ukuran = itemView.findViewById(R.id.ukuran)
            harga = itemView.findViewById(R.id.harga)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            cvSepatu = itemView.findViewById(R.id.cv_sepatu)
        }

    }
    fun deleteSepatu(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, SepatuApi.DELETE_SEPATU+id, Response.Listener { response ->

                val gson = Gson()
                var sepatu = gson.fromJson(response, Sepatu::class.java)
//                if(sepatu != null)
//                    Toast.makeText(this, "Sepatu Berhasil Dihapus", Toast.LENGTH_SHORT).show()
//
            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
//                    Toast.makeText(requireActivity(), errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception){
//                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                }
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }
        queue!!.add(stringRequest)
    }
}