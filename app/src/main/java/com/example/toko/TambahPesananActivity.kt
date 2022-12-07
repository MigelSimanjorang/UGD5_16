package com.example.toko

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.api.SepatuApi
import com.example.toko.databinding.ActivityPesananBinding
import com.example.toko.databinding.ActivityTambahPesananBinding
import com.example.toko.models.Pesanan
import com.example.toko.models.User
import com.example.toko.room.Buy
import com.example.toko.room.Constant
import com.example.toko.room.SepatuDB
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_tambah_pesanan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class TambahPesananActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }

    private val id = "idKey"
    private val myPreference = "myPref"
    var sharedPreferences: SharedPreferences? = null
    private lateinit var binding: ActivityTambahPesananBinding

    private var noteId: Int = 0
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        binding = ActivityTambahPesananBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        queue = Volley.newRequestQueue(this)
        val id = intent.getLongExtra("id", -1)

        if(id == -1L){
            binding.tvTitle.setText("Tambah Pesanan")
            binding.btnSave.setOnClickListener {
                tambahPesanan()
            }
        }else{
            binding.tvTitle.setText("Edit Pesanan")
            getPesananById(id)

            binding.btnSave.setOnClickListener {
                updatePesanan(id)
            }
        }
        btn_cancel.setOnClickListener {
            finish()
        }
    }

    private fun getPesananById(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, SepatuApi.GET_BY_ID_PESANAN + id,
                { response ->
                    val pesanan = Gson().fromJson(response, Pesanan::class.java)

                    binding!!.namaPesanan.setText(pesanan.namaPesanan)
                    binding!!.jumlahPesanan.setText(pesanan.jumlahPesanan)
                    Toast.makeText(this@TambahPesananActivity,"Pesanan berhasil diambil", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener{ error ->
                    try{
                        val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this,
                            errors.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception){
                        Toast.makeText(this@TambahPesananActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun tambahPesanan() {
        val pesananText: EditText = findViewById(R.id.nama_pesanan)
        val jumlahPesananText: EditText = findViewById(R.id.jumlah_pesanan)

        val pesanan = Pesanan(
            pesananText.text.toString(),
            jumlahPesananText.text.toString()
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, SepatuApi.ADD_PESANAN, Response.Listener { response ->
                val movePesanan = Intent(this@TambahPesananActivity, PesananActivity::class.java)
                val gson = Gson()
                var pesanan = gson.fromJson(response, Pesanan::class.java)

                if(pesanan != null)
                    Toast.makeText(this@TambahPesananActivity, "Pesanan berhasil ditambahkan", Toast.LENGTH_SHORT).show()

                startActivity(movePesanan)
                finish()
            }, Response.ErrorListener { error ->
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@TambahPesananActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(pesanan)

                    println(requestBody)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

        queue!!.add(stringRequest)
    }

    private fun updatePesanan(id: Long){
        val pesananText: EditText = findViewById(R.id.nama_pesanan)
        val jumlahPesananText: EditText = findViewById(R.id.jumlah_pesanan)

        val pesanan = Pesanan(
            pesananText.text.toString(),
            jumlahPesananText.text.toString()
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, SepatuApi.UPDATE_PESANAN + id, Response.Listener { response ->
                val gson = Gson()
                var pesanan = gson.fromJson(response, Pesanan::class.java)

                if(pesanan != null)
                    Toast.makeText(this@TambahPesananActivity, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }, Response.ErrorListener { error ->
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@TambahPesananActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }){
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(pesanan)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
        queue!!.add(stringRequest)
    }
}