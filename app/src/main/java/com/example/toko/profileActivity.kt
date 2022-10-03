package com.example.toko

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class profileActivity : AppCompatActivity() {
    lateinit var mBundle: Bundle
    lateinit var ListSepatu: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ListSepatu = findViewById(R.id.ListSepatu)
        ListSepatu.setOnClickListener{
            val move = Intent(this, ShowSepatuActivity::class.java)
            startActivity(move)
        }
        mBundle = intent.getBundleExtra("User")!!
        setData()
    }

    fun setData() {
        val username: TextView = findViewById(R.id.etSessionUsername)
        val email: TextView = findViewById(R.id.etSessionEmail)
        val tanggalLahir: TextView = findViewById(R.id.etSessionTanggalLahir)
        val nomorTelepon: TextView = findViewById(R.id.etSessionNomorTelepon)
        username.text = mBundle.getString("username")
        email.text = mBundle.getString("email")
        tanggalLahir.text = mBundle.getString("tanggalLahir")
        nomorTelepon.text = mBundle.getString("noTelepon")
    }
}