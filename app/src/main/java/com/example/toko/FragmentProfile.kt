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
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.api.SepatuApi
import com.example.toko.databinding.FragmentProfileBinding
import com.example.toko.models.User
import com.google.gson.Gson
//import com.example.toko.room.SepatuDB

import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class FragmentProfile: Fragment() {
    private val myPreference = "login"
    private val id = "idKey"
    var sharedPreferences: SharedPreferences? = null
    private var queue: RequestQueue? = null

    private lateinit var showUsername: TextView
    private lateinit var showEmail: TextView
    private lateinit var showTanggal: TextView
    private lateinit var showNomorHP: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

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
        queue = Volley.newRequestQueue(requireActivity())
        val id = sharedPreferences!!.getInt("id", -1)
        showProfile(id)

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

    private fun showProfile(id: Int) {
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, SepatuApi.getUserById + id, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var user = gson.fromJson(jsonObject.getJSONObject("data").toString(), User::class.java)
                println(user.username)

                showUsername.setText(user.username)
                showEmail.setText(user.email)
                showTanggal.setText(user.tglLahir)
                showNomorHP.setText(user.noTelepon)

            },Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
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
}