package com.example.toko.adapters

import android.content.Context
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
import com.example.toko.HomeActivity
import com.example.toko.R
import com.example.toko.api.SepatuApi
import com.example.toko.models.KaosKaki
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class KaosKakiAdapter(private var kaosKakiList: List<KaosKaki>, context: Context):
    RecyclerView.Adapter<KaosKakiAdapter.ViewHolder>(), Filterable {

    private var filteredKaosKakiList: MutableList<KaosKaki>
    private val context: Context
    private var queue: RequestQueue? = null

    init {
        filteredKaosKakiList = ArrayList(kaosKakiList)
        this.context = context
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_kaos_kaki, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredKaosKakiList.size
    }

    fun setKaosKakiList(kaosKakiList: Array<KaosKaki>){
        this.kaosKakiList = kaosKakiList.toList()
        filteredKaosKakiList = kaosKakiList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kaosKaki = filteredKaosKakiList[position]
        holder.nama_kaosKaki.text = kaosKaki.namaKaosKaki
        holder.jumlah.text =  kaosKaki.jumlah
        holder.ukuran.text =  kaosKaki.ukuran
        holder.harga.text =   kaosKaki.harga

        holder.btnDelete.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin menghapus Kaos Kaki ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus"){_,_ ->
                    if (context is HomeActivity) kaosKaki.id?.let { it1 -> deleteKaosKaki(it1)
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
                val filtered: MutableList<KaosKaki> = java.util.ArrayList()
                if(charSequenceString.isEmpty()){
                    filtered.addAll(kaosKakiList)
                }else{
                    for (kaosKaki in kaosKakiList){
                        if(kaosKaki.namaKaosKaki.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))

                        )filtered.add(kaosKaki)

                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults

            }

            override fun publishResults( CharSequence: CharSequence, filterResults: FilterResults) {
                filteredKaosKakiList.clear()
                filteredKaosKakiList.addAll(filterResults.values as List<KaosKaki>)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nama_kaosKaki: TextView
        var jumlah: TextView
        var ukuran: TextView
        var harga: TextView
        var btnDelete: ImageButton
        var cvKaosKaki: CardView

        init {
            nama_kaosKaki = itemView.findViewById(R.id.nama_kaosKaki)
            jumlah = itemView.findViewById(R.id.jumlah)
            ukuran = itemView.findViewById(R.id.ukuran)
            harga = itemView.findViewById(R.id.harga)
            btnDelete = itemView.findViewById(R.id.btnDelete)
            cvKaosKaki = itemView.findViewById(R.id.cv_kaosKaki)
        }

    }
    fun deleteKaosKaki(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, SepatuApi.DELETE_KAOSKAKI+id, Response.Listener { response ->

                val gson = Gson()
                var kaosKaki = gson.fromJson(response, KaosKaki::class.java)
//                if(KAOS != null)
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