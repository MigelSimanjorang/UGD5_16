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
import com.example.toko.adapters.KaosKakiAdapter

import com.example.toko.api.SepatuApi
import com.example.toko.models.KaosKaki
import com.example.toko.models.Sepatu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sepatu.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class FragmentKaosKaki : Fragment() {
    private var srKaosKaki: SwipeRefreshLayout? = null
    private var adapter: KaosKakiAdapter? = null
    private var svKaosKaki: SearchView? = null
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
        return inflater.inflate(R.layout.fragment_kaos_kaki, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setContentView(R.layout.fragment_sepatu)

        queue = Volley.newRequestQueue(requireContext())
        srKaosKaki = view.findViewById(R.id.sr_kaosKaki)
        svKaosKaki = view.findViewById(R.id.sv_kaosKaki)

        srKaosKaki?.setOnRefreshListener (SwipeRefreshLayout.OnRefreshListener { allKaosKaki() })
        svKaosKaki?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter!!.filter.filter(p0)
                return false
            }
        })


        btn_create.setOnClickListener() {
            val intent = Intent(context, TambahKaosKakiActivity::class.java)
            startActivity(intent)
        }

        val rvKaosKaki = view.findViewById<RecyclerView>(R.id.rv_kaosKaki)
        adapter = KaosKakiAdapter(ArrayList(), requireContext())
        rvKaosKaki.layoutManager = LinearLayoutManager(requireContext())
        rvKaosKaki.adapter = adapter
        allKaosKaki()
    }

    private fun allKaosKaki(){
        srKaosKaki!!.isRefreshing = true
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, SepatuApi.GET_ALL_KAOSKAKI, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)

                var kaosKaki : Array<KaosKaki> = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(),
                    Array<KaosKaki>::class.java)

                adapter!!.setKaosKakiList(kaosKaki)
                adapter!!.filter.filter(svKaosKaki!!.query)
                srKaosKaki!!.isRefreshing = false

                if(!kaosKaki.isEmpty())
                    Toast.makeText(requireActivity(), "Kaos Kaki berhasil diambil", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireActivity(), "Kaos Kaki Kosong!", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                srKaosKaki!!.isRefreshing = false
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

    public fun deleteKaosKaki(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, SepatuApi.DELETE_KAOSKAKI+id, Response.Listener { response ->

                val gson = Gson()
                var kaosKaki = gson.fromJson(response, KaosKaki::class.java)
                if(kaosKaki != null)
                    Toast.makeText(requireActivity(), "Kaos Kaki Berhasil Dihapus", Toast.LENGTH_SHORT).show()

                allKaosKaki()
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