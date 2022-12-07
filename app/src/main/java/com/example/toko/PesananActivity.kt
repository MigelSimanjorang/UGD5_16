package com.example.toko

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
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
import com.example.toko.adapters.PesananAdapter
import com.example.toko.api.SepatuApi
import com.example.toko.models.Pesanan
import com.example.toko.room.Buy
import com.example.toko.room.Constant
import com.example.toko.room.SepatuDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pesanan.*
import kotlinx.android.synthetic.main.item_pesanan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class PesananActivity : AppCompatActivity() {
    private var srPesanan: SwipeRefreshLayout? = null
    private var adapter: PesananAdapter? = null
    private var svPesanan: SearchView? = null
    private var queue: RequestQueue? = null

    val db by lazy { SepatuDB(this) }
    lateinit var noteAdapter: ListSepatuAdapter

    companion object{
        const val LAUNCH_ADD_ACTIVITY = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)

        queue = Volley.newRequestQueue(this)
        srPesanan = findViewById(R.id.sr_pesanan)
        svPesanan = findViewById(R.id.sv_pesanan)

        srPesanan?.setOnRefreshListener (SwipeRefreshLayout.OnRefreshListener { allPesanan() })
        svPesanan?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter!!.filter.filter(p0)
                return false
            }
        })

        val fabAdd = findViewById<FloatingActionButton>(R.id.button_create)
        fabAdd.setOnClickListener {
            val i = Intent(this@PesananActivity,  TambahPesananActivity::class.java)
            startActivityForResult(i, LAUNCH_ADD_ACTIVITY)
        }

        val rvPesanan = findViewById<RecyclerView>(R.id.rv_pesanan)
        adapter = PesananAdapter(ArrayList(), this)
        rvPesanan.layoutManager = LinearLayoutManager(this)
        rvPesanan.adapter = adapter
        allPesanan()
    }

    private fun allPesanan(){
        srPesanan!!.isRefreshing = true
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, SepatuApi.GET_ALL_PESANAN, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)

                var pesanan : Array<Pesanan> = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(),
                    Array<Pesanan>::class.java)

                adapter!!.setPesananList(pesanan)
                adapter!!.filter.filter(svPesanan!!.query)
                srPesanan!!.isRefreshing = false

                if(!pesanan.isEmpty())
                    Toast.makeText(this@PesananActivity, "Pesanan berhasil diambil", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this@PesananActivity, "Pesanan Kosong!", Toast.LENGTH_SHORT).show()

            }, Response.ErrorListener { error ->
                srPesanan!!.isRefreshing = false
                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(this@PesananActivity, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(this@PesananActivity, e.message, Toast.LENGTH_SHORT).show()
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

    fun deletePesanan(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, SepatuApi.DELETE_PESANAN+id, Response.Listener { response ->

                val gson = Gson()
                var pesanan = gson.fromJson(response, Pesanan::class.java)
                if(pesanan != null)
                    Toast.makeText(this@PesananActivity, "Pesanan Berhasil Dihapus", Toast.LENGTH_SHORT).show()

                allPesanan()
            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(this@PesananActivity, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception){
                    Toast.makeText(this@PesananActivity, e.message, Toast.LENGTH_SHORT).show()
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
//    override fun onStart() {
//        super.onStart()
//        loadData()
//    }

//    //untuk load data yang tersimpan pada database yang sudah create data
//    fun loadData() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val notes = db.buyDao().getBuy()
//            Log.d("ShowFamily","dbResponse: $notes")
//            withContext(Dispatchers.Main){
//                noteAdapter.setData(notes)
//            }
//        }
//    }
//    fun setupListener() {
//        button_create.setOnClickListener{
//            intentEdit(0, Constant.TYPE_CREATE)
//        }
//    }
//
//    //pick data dari Id yang sebagai primary key
//    fun intentEdit(noteId : Int, intentType: Int){
//        startActivity(
//            Intent(applicationContext, TambahPesananActivity::class.java)
//                .putExtra("intent_id", noteId)
//                .putExtra("intent_type", intentType)
//        )
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == LAUNCH_ADD_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                allPesanan()
            }
        }
    }
}