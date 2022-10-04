package com.example.toko

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.toko.room.SepatuDB
import com.example.toko.room.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class editProfileActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }
    private lateinit var binding: ActivityEditBinding
    private val id = "idKey"
    private val myPreference = "myPref"
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        val view = binding.root
        setContentView(view)
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        val id = sharedPreferences!!.getString(id,"")!!.toInt()
        loadData(id)

        binding.topAppBar.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.button_save -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = db?.userDao()?.updateUser(
                            User(id,binding.inputUsername.text.toString()
                            ,binding.inputEmail.text.toString(),binding.inputPassword.text.toString(),
                            binding.inputTanggalLahir.text.toString(),binding.inputNomorTelepon.text.toString())
                        )
                    }
                    finish()
                    val intent = Intent(this, HomeActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("key","iniTerisi")
                    intent.putExtra("keyBundle",bundle)
                    startActivity(intent)

                    true
                }
                else -> false
            }

        }




    }

    fun loadData(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val user = db?.userDao()?.getUser(id)?.get(0)


            withContext(Dispatchers.Main){
                binding.inputUsername.setText(user?.username)
                binding.inputEmail.setText(user?.email)
                binding.inputNomorTelepon.setText(user?.noTelepon)
                binding.inputTanggalLahir.setText(user?.tanggalLahir)
                binding.inputPassword.setText(user?.password)
            }

        }
    }
}