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
import com.example.toko.databinding.FragmentProfileBinding
import com.example.toko.room.SepatuDB

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentProfile: Fragment() {

    val db by lazy { activity?.let { SepatuDB(it) } }
    private val myPreference = "login"
    private val id = "idKey"
    var sharedPreferences: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        val showUsername: TextView = view.findViewById(R.id.username)
        val showEmail: TextView = view.findViewById(R.id.email)
        val showTanggal: TextView = view.findViewById(R.id.tanggalLahir)
        val showNomorHP: TextView = view.findViewById(R.id.noTelepon)

        CoroutineScope(Dispatchers.IO).launch {
            val user = db?.userDao()?.getUser(sharedPreferences!!.getString(id,"")!!.toInt())?.get(0)
            showUsername.setText(user?.username)
            showEmail.setText(user?.email)
            showTanggal.setText(user?.tanggalLahir)
            showNomorHP.setText(user?.noTelepon)
        }

        btnEditData.setOnClickListener() {
            val intent = Intent(context, EditSepatuActivity::class.java)
            startActivity(intent)
        }
    }
}