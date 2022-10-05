package com.example.toko

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.toko.databinding.ActivityEditProfileBinding
import com.example.toko.databinding.ActivityRegisterBinding
import com.example.toko.room.SepatuDB
import com.example.toko.room.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }

    private lateinit var binding: ActivityEditProfileBinding
    private val id = "idKey"
    private val myPreference = "login"
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        getSupportActionBar()?.hide()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val viewBinding = binding.root
        setContentView(viewBinding)

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        val id = sharedPreferences!!.getString(id,"")!!.toInt()
        boostData(id)

        val calendar = Calendar.getInstance()
        val tahun = calendar.get(Calendar.YEAR)
        val bulan = calendar.get(Calendar.MONTH)
        val hari = calendar.get(Calendar.DAY_OF_MONTH)

        binding.inputTanggalLahir.setOnFocusChangeListener { view, b ->
            val datePicker =
                this?.let { it1 ->
                    DatePickerDialog(it1, DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                        binding.inputTanggalLahir.setText("${dayOfMonth}/${(month.toInt() + 1).toString()}/${year}")
                    }, tahun, bulan, hari)
                }
            if(b){
                datePicker?.show()
            }else{
                datePicker?.hide()
            }
        }

        binding.btnSave.setOnClickListener {
            var checkRegis = false

            if (binding.inputUsername.text.toString().isEmpty() && binding.inputPassword.text.toString().isEmpty() && binding.inputEmail.text.toString().isEmpty() && binding.inputTanggalLahir.text.toString().isEmpty() && binding.inputNoTelepon.text.toString().isEmpty()) {
                if (binding.inputUsername.text.toString().isEmpty()) {
                    binding.inputUsername.setError("Username must be filled with Text")
                }
                if (binding.inputPassword.text.toString().isEmpty()) {
                    binding.inputPassword.setError("Password must be filled with Text")
                }
                if (binding.inputEmail.text.toString().isEmpty()) {
                    binding.inputEmail.setError("Email must be filled with Text")
                }
                if (binding.inputTanggalLahir.text.toString().isEmpty()) {
                    binding.inputTanggalLahir.setError("Tanggal Lahir must be filled with Text")
                }
                if (binding.inputNoTelepon.text.toString().isEmpty()) {
                    binding.inputNoTelepon.setError("No Telepon must be filled with Text")
                }
            } else {
                checkRegis = true
                setupListener()
            }

            if (!checkRegis) return@setOnClickListener
            finish()
//            (activity as HomeActivity).changeFragment(FragmentProfile())
        }
    }

    private fun setupListener(){
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString(id, "")
        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().updateUser(User(id!!.toInt(),
                binding.inputUsername.text.toString(),
                binding.inputPassword.text.toString(),
                binding.inputEmail.text.toString(),
                binding.inputTanggalLahir.text.toString(),
                binding.inputNoTelepon.text.toString()))
        }
        finish()
    }

    fun boostData(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val user = db?.userDao()?.getUser(id)?.get(0)

            withContext(Dispatchers.Main){
                binding.layoutUsername.editText?.setText(user?.username)
                binding.layoutPassword.editText?.setText(user?.password)
                binding.layoutEmail.editText?.setText(user?.email)
                binding.layoutTanggalLahir.editText?.setText(user?.tanggalLahir)
                binding.layoutNoTelepon.editText?.setText(user?.noTelepon)
            }
        }
    }
}