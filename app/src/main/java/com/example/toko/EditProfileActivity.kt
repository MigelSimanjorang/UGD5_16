package com.example.toko

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.api.SepatuApi
import com.example.toko.databinding.ActivityEditProfileBinding
import com.example.toko.databinding.ActivityRegisterBinding
import com.example.toko.databinding.FragmentProfileBinding
import com.example.toko.models.User
//import com.example.toko.room.SepatuDB
//import com.example.toko.room.User
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val id = "idKey"
    private val myPreference = "login"
    var sharedPreferences: SharedPreferences? = null
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        getSupportActionBar()?.hide()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val viewBinding = binding.root
        setContentView(viewBinding)

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        queue = Volley.newRequestQueue(this@EditProfileActivity)

        val id = sharedPreferences!!.getInt("id", -1)
        getUserById(id)

        val calendar = Calendar.getInstance()
        val tahun = calendar.get(Calendar.YEAR)
        val bulan = calendar.get(Calendar.MONTH)
        val hari = calendar.get(Calendar.DAY_OF_MONTH)

//        binding.inputTanggalLahir.setOnFocusChangeListener { view, b ->
//            val datePicker =
//                this?.let { it1 ->
//                    DatePickerDialog(it1, DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
//                        binding.inputTanggalLahir.setText("${dayOfMonth}/${(month.toInt() + 1).toString()}/${year}")
//                    }, tahun, bulan, hari)
//                }
//            if(b){
//                datePicker?.show()
//            }else{
//                datePicker?.hide()
//            }
//        }

        binding.btnSave.setOnClickListener {
            var checkRegis = false

            if ((binding.inputUsername.text.toString().isEmpty() && binding.inputPassword.text.toString().isEmpty() && binding.inputEmail.text.toString().isEmpty() && binding.inputTanggalLahir.text.toString().isEmpty() && binding.inputNoTelepon.text.toString().isEmpty()) || !(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches())) {
                if (binding.inputUsername.text.toString().isEmpty()) {
                    binding.inputUsername.setError("Username must be filled with Text")
                }
                if (binding.inputPassword.text.toString().isEmpty()) {
                    binding.inputPassword.setError("Password must be filled with Text")
                }
                if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches())) {
                    binding.inputEmail.setError("The email is not in the correct format")
                }
                if (binding.inputTanggalLahir.text.toString().isEmpty()) {
                    binding.inputTanggalLahir.setError("Tanggal Lahir must be filled with Text")
                }
                if (binding.inputNoTelepon.text.toString().isEmpty()) {
                    binding.inputNoTelepon.setError("No Telepon must be filled with Text")
                }
            } else {
                checkRegis = true
            }

            if (!checkRegis) return@setOnClickListener
            updateUser(id)
            finish()
//            (activity as HomeActivity).changeFragment(FragmentProfile())
        }
    }

    private fun getUserById(id: Int) {
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, SepatuApi.getUserById + id, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var user = gson.fromJson(jsonObject.getJSONObject("data").toString(), User::class.java)
                println(user.username)

                binding.inputUsername.setText(user.username)
                binding.inputPassword.setText(user.password)
                binding.inputEmail.setText(user.email)
                binding.inputTanggalLahir.setText(user.tglLahir)
                binding.inputNoTelepon.setText(user.noTelepon)

            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(this, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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

    private fun updateUser(id: Int) {
        if (binding.inputUsername.text.toString().isEmpty()) {
            FancyToast.makeText(this@EditProfileActivity,"Username is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (binding.inputPassword.text.toString().isEmpty()) {
            FancyToast.makeText(this@EditProfileActivity,"Password is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches())) {
            FancyToast.makeText(this@EditProfileActivity,"Email is not formatted !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (binding.inputTanggalLahir.text.toString().isEmpty()) {
            FancyToast.makeText(this@EditProfileActivity,"Tanggal lahir is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (binding.inputNoTelepon.text.toString().isEmpty()) {
            FancyToast.makeText(this@EditProfileActivity,"No Telepon is Empty !", FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val update = User(
                binding.inputUsername.text.toString(),
                binding.inputPassword.text.toString(),
                binding.inputEmail.text.toString(),
                binding.inputTanggalLahir.text.toString(),
                binding.inputNoTelepon.text.toString(),
            )

            val stringRequest: StringRequest = object :
                StringRequest(Method.PUT, SepatuApi.updateUser + id, Response.Listener { response ->
                    val gson = Gson()
                    var update = gson.fromJson(response, User::class.java)

                    if(update != null)
                        Toast.makeText(this@EditProfileActivity, "Data Berhasil Update", Toast.LENGTH_SHORT).show()

                }, Response.ErrorListener { error ->
                    try {
                        Toast.makeText(
                            this@EditProfileActivity,
                            error.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@EditProfileActivity, e.message, Toast.LENGTH_SHORT).show()
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
                    val requestBody = gson.toJson(update)
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