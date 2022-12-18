package com.example.toko

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.adapters.OutfitAdapter
import com.example.toko.api.SepatuApi
import com.example.toko.models.Outfit
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_outfit.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class FragmentOutfit : Fragment() {
    private var srOutfit: SwipeRefreshLayout? = null
    private var adapter: OutfitAdapter? = null
    private var svOutfit: SearchView? = null
    private var queue: RequestQueue? = null

    companion object{
        const val LAUNCH_ADD_ACTIVITY = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_barang.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_outfit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queue = Volley.newRequestQueue(requireContext())
        srOutfit = view.findViewById(R.id.sr_outfit)
        svOutfit = view.findViewById(R.id.sv_outfit)

        srOutfit?.setOnRefreshListener (SwipeRefreshLayout.OnRefreshListener { allOutfit() })
        svOutfit?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter!!.filter.filter(p0)
                return false
            }
        })

        btn_create.setOnClickListener() {
            val intent = Intent(context, TambahOutfitActivity::class.java)
            startActivity(intent)
        }

        val rvOutfit = view.findViewById<RecyclerView>(R.id.rv_outfit)
        adapter = OutfitAdapter(ArrayList(), requireContext())
        rvOutfit.layoutManager = LinearLayoutManager(requireContext())
        rvOutfit.adapter = adapter
        allOutfit()
    }

    private fun allOutfit(){
        srOutfit!!.isRefreshing = true
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, SepatuApi.GET_ALL_OUTFIT, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)

                var outfit : Array<Outfit> = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(),
                    Array<Outfit>::class.java)

                adapter!!.setOutfitList(outfit)
                adapter!!.filter.filter(svOutfit!!.query)
                srOutfit!!.isRefreshing = false

                if(!outfit.isEmpty())
                    Toast.makeText(requireActivity(), "Outfit berhasil diambil", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireActivity(), "Outfit Kosong!", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                srOutfit!!.isRefreshing = false
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(requireActivity(), errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }

        }
        queue!!.add(stringRequest)
    }

//    fun deleteOutfit(id: Long){
//        val stringRequest: StringRequest = object :
//            StringRequest(Method.DELETE, SepatuApi.DELETE_SEPATU+id, Response.Listener { response ->
//
//                val gson = Gson()
//                var sepatu = gson.fromJson(response, Sepatu::class.java)
//                if(sepatu != null)
//                    Toast.makeText(requireActivity(), "Sepatu Berhasil Dihapus", Toast.LENGTH_SHORT).show()
//
//                allSepatu()
//            }, Response.ErrorListener { error ->
//                try {
//                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
//                    val errors = JSONObject(responseBody)
//                    Toast.makeText(requireActivity(), errors.getString("message"), Toast.LENGTH_SHORT).show()
//                } catch (e: java.lang.Exception){
//                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
//                }
//            }){
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val headers = java.util.HashMap<String, String>()
//                headers["Accept"] = "application/json"
//                return headers
//            }
//        }
//        queue!!.add(stringRequest)
//    }
}