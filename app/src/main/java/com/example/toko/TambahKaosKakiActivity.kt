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
import com.example.toko.databinding.ActivityTambahKaosKakiBinding
import com.example.toko.databinding.ActivityTambahSepatuBinding
import com.example.toko.models.KaosKaki
import com.example.toko.models.Sepatu
import com.example.toko.room.SepatuDB
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_tambah_sepatu.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.HashMap

class TambahKaosKakiActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }

    private val id = "idKey"
    private val myPreference = "myPref"
    var sharedPreferences: SharedPreferences? = null
    private lateinit var binding: ActivityTambahKaosKakiBinding

    private var noteId: Int = 0
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()

        binding = ActivityTambahKaosKakiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        queue = Volley.newRequestQueue(this)
        val id = intent.getLongExtra("id", -1)

        if(id == -1L){
            binding.tvTitle.setText("Tambah KaosKaki")
            binding.btnSave.setOnClickListener {
                tambahKaosKaki()
            }
        }else{
            binding.tvTitle.setText("Edit KaosKaki")
            getKaosKakiById(id)

            binding.btnSave.setOnClickListener {
                updateKaosKaki(id)
            }
        }
        btn_cancel.setOnClickListener {
            finish()
        }
    }

    private fun getKaosKakiById(id: Long){
        val stringRequest: StringRequest = object :
            StringRequest(
                Method.GET, SepatuApi.GET_BY_ID_KAOSKAKI + id,
                { response ->
                    val kaosKaki = Gson().fromJson(response, KaosKaki::class.java)

                    binding!!.namaKaosKaki.setText(kaosKaki.namaKaosKaki)
                    binding!!.jumlah.setText(kaosKaki.jumlah)
                    binding!!.ukuran.setText(kaosKaki.ukuran)
                    binding!!.harga.setText(kaosKaki.harga)
                    Toast.makeText(this@TambahKaosKakiActivity,"KaosKaki berhasil diambil", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@TambahKaosKakiActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun tambahKaosKaki() {
        val namaKaosKakiText: EditText = findViewById(R.id.nama_kaosKaki)
        val jumlahText: EditText = findViewById(R.id.jumlah)
        val ukuranText: EditText = findViewById(R.id.ukuran)
        val hargaText: EditText = findViewById(R.id.harga)

        if (namaKaosKakiText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Nama KaosKaki is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (jumlahText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Jumlah is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (ukuranText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Ukuran is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (hargaText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Harga is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val kaosKaki = KaosKaki(
                namaKaosKakiText.text.toString(),
                jumlahText.text.toString(),
                ukuranText.text.toString(),
                hargaText.text.toString()
            )

            val stringRequest: StringRequest =
                object: StringRequest(Method.POST, SepatuApi.ADD_KAOSKAKI, Response.Listener { response ->
//                    val moveSepatu = Intent(this@TambahSepatuActivity, SepatuActivity::class.java)
                    val gson = Gson()
                    var kaosKaki = gson.fromJson(response, KaosKaki::class.java)

                    if(kaosKaki != null)
                        Toast.makeText(this@TambahKaosKakiActivity, "Kaos Kaki berhasil ditambahkan", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this@TambahKaosKakiActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(kaosKaki)

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

    private fun updateKaosKaki(id: Long){
        val namaKaosKakiText: EditText = findViewById(R.id.nama_kaosKaki)
        val jumlahText: EditText = findViewById(R.id.jumlah)
        val ukuranText: EditText = findViewById(R.id.ukuran)
        val hargaText: EditText = findViewById(R.id.harga)

        if (namaKaosKakiText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Nama Sepatu is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (jumlahText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Jumlah is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (ukuranText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Ukuran is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (hargaText.text.toString().isEmpty()) {
            FancyToast.makeText(this@TambahKaosKakiActivity,"Harga is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val kaosKaki = KaosKaki(
                namaKaosKakiText.text.toString(),
                jumlahText.text.toString(),
                ukuranText.text.toString(),
                hargaText.text.toString()
            )

            val stringRequest: StringRequest =
                object: StringRequest(Method.PUT, SepatuApi.UPDATE_KAOSKAKI + id, Response.Listener { response ->
                    val gson = Gson()
                    var kaosKaki = gson.fromJson(response, KaosKaki::class.java)

                    if(kaosKaki != null)
                        Toast.makeText(this@TambahKaosKakiActivity, "Data berhasil diubah", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this@TambahKaosKakiActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(kaosKaki)
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