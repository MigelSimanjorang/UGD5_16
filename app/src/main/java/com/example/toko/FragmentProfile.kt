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
import com.example.toko.databinding.FragmentProfileBinding
import com.example.toko.room.SepatuDB

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentProfile : Fragment() {
    val db by lazy { activity?.let { SepatuDB(it) } }
    private val id = "idKey"
    private val myPreference = "myPref"
    var sharedPreferences: SharedPreferences? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val user = db?.userDao()?.getUser(sharedPreferences!!.getString(id,"")!!.toInt())?.get(0)
            binding.nama.setText(user?.username)
            binding.email.setText(user?.email)
            binding.noTelp.setText(user?.noTelepon)
            binding.tglLahir.setText(user?.tanggalLahir)
            binding.btnEditData.setOnClickListener {
                val moveEdit = Intent(activity, EditSepatuActivity::class.java)
                startActivity(moveEdit)
                activity?.finish()
            }
        }
    }


}