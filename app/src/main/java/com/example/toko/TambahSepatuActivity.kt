package com.example.toko

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.api.SepatuApi
import com.example.toko.databinding.ActivityTambahSepatuBinding
import com.example.toko.models.Sepatu
import com.example.toko.room.SepatuDB
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_tambah_sepatu.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class TambahSepatuActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }

    private val id = "idKey"
    private val myPreference = "myPref"
    var sharedPreferences: SharedPreferences? = null
    private lateinit var binding: ActivityTambahSepatuBinding

    private var noteId: Int = 0
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        binding = ActivityTambahSepatuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        queue = Volley.newRequestQueue(this)
        val id = intent.getLongExtra("id", -1)

        if(id == -1L){
            binding.tvTitle.setText("Tambah Sepatu")
            binding.btnSave.setOnClickListener {
                tambahSepatu()
            }
        }else{
            binding.tvTitle.setText("Edit Sepatu")
            getSepatuById(id)

            binding.btnSave.setOnClickListener {
                updateSepatu(id)
            }
        }
        btn_cancel.setOnClickListener {
            finish()
        }
    }

    private fun getSepatuById(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, SepatuApi.GET_BY_ID_SEPATU + id, { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var sepatu = gson.fromJson(jsonObject.getJSONObject("data").toString(), Sepatu::class.java)
                println(sepatu.namaSepatu)

                binding!!.namaSepatu.setText(sepatu.namaSepatu)
                binding!!.jumlah.setText(sepatu.jumlah)
                binding!!.ukuran.setText(sepatu.ukuran)
                binding!!.harga.setText(sepatu.harga)

            }, Response.ErrorListener{ error ->
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@TambahSepatuActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun tambahSepatu() {
        val namaSepatuText: EditText = findViewById(R.id.nama_sepatu)
        val jumlahText: EditText = findViewById(R.id.jumlah)
        val ukuranText: EditText = findViewById(R.id.ukuran)
        val hargaText: EditText = findViewById(R.id.harga)

        if (namaSepatuText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Nama Sepatu is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (jumlahText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Jumlah is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (ukuranText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Ukuran is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (hargaText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Harga is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val sepatu = Sepatu(
                namaSepatuText.text.toString(),
                jumlahText.text.toString(),
                ukuranText.text.toString(),
                hargaText.text.toString()
            )

            val stringRequest: StringRequest =
                object: StringRequest(Method.POST, SepatuApi.ADD_SEPATU, Response.Listener { response ->
//                    val moveSepatu = Intent(this@TambahSepatuActivity, SepatuActivity::class.java)
                    val gson = Gson()
                    var sepatu = gson.fromJson(response, Sepatu::class.java)

                    if(sepatu != null)
                        Toast.makeText(this@TambahSepatuActivity, "Sepatu berhasil ditambahkan", Toast.LENGTH_SHORT).show()

//                    startActivity(movePesanan)
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
                        Toast.makeText(this@TambahSepatuActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(sepatu)

                        println(requestBody)
                        return requestBody.toByteArray(StandardCharsets.UTF_8)
                    }

                    override fun getBodyContentType(): String {
                        return "application/json"
                    }
                }

            queue!!.add(stringRequest)
        }
    }

    private fun updateSepatu(id: Long){
        val namaSepatuText: EditText = findViewById(R.id.nama_sepatu)
        val jumlahText: EditText = findViewById(R.id.jumlah)
        val ukuranText: EditText = findViewById(R.id.ukuran)
        val hargaText: EditText = findViewById(R.id.harga)

        if (namaSepatuText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Nama Sepatu is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (jumlahText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Jumlah is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (ukuranText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Ukuran is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (hargaText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahSepatuActivity,"Harga is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val sepatu = Sepatu(
                namaSepatuText.text.toString(),
                jumlahText.text.toString(),
                ukuranText.text.toString(),
                hargaText.text.toString()
            )

            val stringRequest: StringRequest =
                object: StringRequest(Method.PUT, SepatuApi.UPDATE_SEPATU + id, Response.Listener { response ->
                    val gson = Gson()
                    var sepatu = gson.fromJson(response, Sepatu::class.java)

                    if(sepatu != null)
                        Toast.makeText(this@TambahSepatuActivity, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this@TambahSepatuActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(sepatu)
                        return requestBody.toByteArray(StandardCharsets.UTF_8)
                    }

                    override fun getBodyContentType(): String {
                        return "application/json"
                    }
                }
            queue!!.add(stringRequest)
        }
    }
}