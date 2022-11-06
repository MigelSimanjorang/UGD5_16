package com.example.toko

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.toko.room.SepatuDB

import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentProfile: Fragment() {

    val db by lazy { activity?.let { SepatuDB(it) } }
    private val myPreference = "login"
    private val id = "idKey"
    var sharedPreferences: SharedPreferences? = null

    private lateinit var showUsername: TextView
    private lateinit var showEmail: TextView
    private lateinit var showTanggal: TextView
    private lateinit var showNomorHP: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch{
            val user = db?.userDao()?.getUser(sharedPreferences!!.getString(id,"")!!.toInt())?.get(0)
            withContext(Dispatchers.Main) {
                showUsername.setText(user?.username)
                showEmail.setText(user?.email)
                showTanggal.setText(user?.tanggalLahir)
                showNomorHP.setText(user?.noTelepon)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        showUsername = view.findViewById(R.id.username)
        showEmail = view.findViewById(R.id.email)
        showTanggal = view.findViewById(R.id.tanggalLahir)
        showNomorHP = view.findViewById(R.id.noTelepon)

        btnPesanan.setOnClickListener() {
            val intent = Intent(context, PesananActivity::class.java)
            startActivity(intent)
        }

        btnEditProfile.setOnClickListener() {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val camera: Button = view.findViewById(R.id.btnCamera)
        camera.setOnClickListener() {
            val intent = Intent(context, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}