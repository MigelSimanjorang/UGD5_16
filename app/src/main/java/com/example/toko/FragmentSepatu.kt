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
import com.example.toko.adapters.SepatuAdapter
import com.example.toko.api.SepatuApi
import com.example.toko.models.Sepatu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sepatu.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class FragmentSepatu : Fragment() {
    private var srSepatu: SwipeRefreshLayout? = null
    private var adapter: SepatuAdapter? = null
    private var svSepatu: SearchView? = null
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
        return inflater.inflate(R.layout.fragment_sepatu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queue = Volley.newRequestQueue(requireContext())
        srSepatu = view.findViewById(R.id.sr_sepatu)
        svSepatu = view.findViewById(R.id.sv_sepatu)

        srSepatu?.setOnRefreshListener (SwipeRefreshLayout.OnRefreshListener { allSepatu() })
        svSepatu?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter!!.filter.filter(p0)
                return false
            }
        })

        btn_create.setOnClickListener() {
            val intent = Intent(context, TambahSepatuActivity::class.java)
            startActivity(intent)
        }

        val rvSepatu = view.findViewById<RecyclerView>(R.id.rv_sepatu)
        adapter = SepatuAdapter(ArrayList(), requireContext())
        rvSepatu.layoutManager = LinearLayoutManager(requireContext())
        rvSepatu.adapter = adapter
        allSepatu()
    }

    private fun allSepatu(){
        srSepatu!!.isRefreshing = true
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, SepatuApi.GET_ALL_SEPATU, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)

                var sepatu : Array<Sepatu> = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(),
                    Array<Sepatu>::class.java)

                adapter!!.setSepatuList(sepatu)
                adapter!!.filter.filter(svSepatu!!.query)
                srSepatu!!.isRefreshing = false

                if(!sepatu.isEmpty())
                    Toast.makeText(requireActivity(), "Sepatu berhasil diambil", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireActivity(), "Sepatu Kosong!", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                srSepatu!!.isRefreshing = false
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

    fun deleteSepatu(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, SepatuApi.DELETE_SEPATU+id, Response.Listener { response ->

                val gson = Gson()
                var sepatu = gson.fromJson(response, Sepatu::class.java)
                if(sepatu != null)
                    Toast.makeText(requireActivity(), "Sepatu Berhasil Dihapus", Toast.LENGTH_SHORT).show()

                allSepatu()
            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(requireActivity(), errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception){
                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
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