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
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.FragmentOutfit
import com.example.toko.HomeActivity
import com.example.toko.R
import com.example.toko.TambahOutfitActivity
import com.example.toko.api.SepatuApi
import com.example.toko.models.Outfit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class OutfitAdapter(private var outfitList: List<Outfit>, context: Context):
    RecyclerView.Adapter<OutfitAdapter.ViewHolder>(), Filterable {

    private var filteredOutfitList: MutableList<Outfit>
    private val context: Context
    private var queue: RequestQueue? = null

    init {
        filteredOutfitList = ArrayList(outfitList)
        this.context = context
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_outfit, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredOutfitList.size
    }

    fun setOutfitList(outfitList: Array<Outfit>){
        this.outfitList = outfitList.toList()
        filteredOutfitList = outfitList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outfit = filteredOutfitList[position]
        holder.nama_outfit.text = outfit.namaOutfit
        holder.jumlah.text =  outfit.jumlah
        holder.ukuran.text =  outfit.ukuran
        holder.harga.text =  outfit.harga

        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus outfit ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if (context is HomeActivity) outfit.id?.let { it1 -> deleteOutfit(it1)
                    }
                }
                .show()
        }

        holder.cvOutfit.setOnClickListener {
            val intent = Intent(context, TambahOutfitActivity::class.java)
            intent.putExtra("id", outfit.id)
            if(context is HomeActivity)
                context.startActivityForResult(intent, FragmentOutfit.LAUNCH_ADD_ACTIVITY)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<Outfit> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(outfitList)
                }else{
                    for (outfit in outfitList){
                        if(outfit.namaOutfit.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))

                        )filtered.add(outfit)

                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults

            }

            override fun publishResults( CharSequence: CharSequence, filterResults: FilterResults) {
                filteredOutfitList.clear()
                filteredOutfitList.addAll(filterResults.values as List<Outfit>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nama_outfit: TextView
        var jumlah: TextView
        var ukuran: TextView
        var harga: TextView
        var btnDelete: ImageButton
        var cvOutfit: CardView

        init {
            nama_outfit = itemView.findViewById(R.id.nama_outfit)
            jumlah = itemView.findViewById(R.id.jumlah)
            ukuran = itemView.findViewById(R.id.ukuran)
            harga = itemView.findViewById(R.id.harga)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            cvOutfit = itemView.findViewById(R.id.cv_outfit)
        }

    }
    fun deleteOutfit(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, SepatuApi.DELETE_OUTFIT+id, Response.Listener { response ->

                val gson = Gson()
                var outfit = gson.fromJson(response, Outfit::class.java)
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