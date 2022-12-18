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
import com.example.toko.databinding.ActivityTambahOutfitBinding
import com.example.toko.models.Outfit
import com.example.toko.room.SepatuDB
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_tambah_outfit.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class TambahOutfitActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }

    private val id = "idKey"
    private val myPreference = "myPref"
    var sharedPreferences: SharedPreferences? = null
    private lateinit var binding: ActivityTambahOutfitBinding

    private var noteId: Int = 0
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        binding = ActivityTambahOutfitBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        queue = Volley.newRequestQueue(this)
        val id = intent.getLongExtra("id", -1)

        if(id == -1L){
            binding.tvTitle.setText("Tambah Outfit")
            binding.btnSave.setOnClickListener {
                tambahOutfit()
            }
        }else{
            binding.tvTitle.setText("Edit Outfit")
            getOutfitById(id)

            binding.btnSave.setOnClickListener {
                updateOutfit(id)
            }
        }
        btn_cancel.setOnClickListener {
            finish()
        }
    }

    private fun getOutfitById(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, SepatuApi.GET_BY_ID_OUTFIT + id, { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var outfit = gson.fromJson(jsonObject.getJSONObject("data").toString(), Outfit::class.java)
                println(outfit.namaOutfit)

                binding!!.namaOutfit.setText(outfit.namaOutfit)
                binding!!.jumlah.setText(outfit.jumlah)
                binding!!.ukuran.setText(outfit.ukuran)
                binding!!.harga.setText(outfit.harga)

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
                    Toast.makeText(this@TambahOutfitActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun tambahOutfit() {
        val namaOutfitText: EditText = findViewById(R.id.nama_outfit)
        val jumlahText: EditText = findViewById(R.id.jumlah)
        val ukuranText: EditText = findViewById(R.id.ukuran)
        val hargaText: EditText = findViewById(R.id.harga)

        if (namaOutfitText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Nama Outfit is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (jumlahText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Jumlah is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (ukuranText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Ukuran is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (hargaText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Harga is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val outfit = Outfit(
                namaOutfitText.text.toString(),
                jumlahText.text.toString(),
                ukuranText.text.toString(),
                hargaText.text.toString()
            )

            val stringRequest: StringRequest =
                object: StringRequest(Method.POST, SepatuApi.ADD_OUTFIT, Response.Listener { response ->
                    val gson = Gson()
                    var outfit = gson.fromJson(response, Outfit::class.java)

                    if(outfit != null)
                        Toast.makeText(this@TambahOutfitActivity, "Outfit berhasil ditambahkan", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this@TambahOutfitActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(outfit)

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

    private fun updateOutfit(id: Long){
        val namaOutfitText: EditText = findViewById(R.id.nama_outfit)
        val jumlahText: EditText = findViewById(R.id.jumlah)
        val ukuranText: EditText = findViewById(R.id.ukuran)
        val hargaText: EditText = findViewById(R.id.harga)

        if (namaOutfitText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Nama Outfit is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (jumlahText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Jumlah is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (ukuranText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Ukuran is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (hargaText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahOutfitActivity,"Harga is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val outfit = Outfit(
                namaOutfitText.text.toString(),
                jumlahText.text.toString(),
                ukuranText.text.toString(),
                hargaText.text.toString()
            )

            val stringRequest: StringRequest =
                object: StringRequest(Method.PUT, SepatuApi.UPDATE_OUTFIT + id, Response.Listener { response ->
                    val gson = Gson()
                    var outfit = gson.fromJson(response, Outfit::class.java)

                    if(outfit != null)
                        Toast.makeText(this@TambahOutfitActivity, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this@TambahOutfitActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(outfit)
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