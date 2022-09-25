package com.example.toko

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var mainLayout: ConstraintLayout

    lateinit var  mBundle: Bundle
    lateinit var newUsername: String
    lateinit var newPassword: String
    lateinit var newEmail: String
    lateinit var newTanggalLahir: String
    lateinit var newNoTelepon: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menyembunyikan Action Bar
        getSupportActionBar()?.hide()

        // Hubungkan variabel dengan view di layoutnya
        inputUsername = findViewById(R.id.inputLayoutUsername)
        inputPassword = findViewById(R.id.inputLayoutPassword)
        mainLayout = findViewById(R.id.mainLayout)

        val btnRegister: Button = findViewById(R.id.btnRegister)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        var intent : Intent = intent

        // Mengambil data register ketika sudah register
        if (intent.hasExtra("register")) {
            getBundle()
            setText()
        }

        // Aksi pada btnLogin
        btnLogin.setOnClickListener(View.OnClickListener {
            var checkLogin = false
            val username: String = inputUsername.getEditText()?.getText().toString()
            val password: String = inputPassword.getEditText()?.getText().toString()
            val username2 : String = username

            // EROR HANDLING
            if (intent.hasExtra("register")) {
                if (username.isEmpty()) {
                    inputUsername.setError("Username must be filled with Text")
                    checkLogin = false
                }
                if (password.isEmpty()) {
                    inputPassword.setError("Password must ben filled with text")
                    checkLogin = false
                }

                if (username == "admin" && password == "admin") {
                    checkLogin = true
                }
                if (username == newUsername && password == newPassword) {
                    checkLogin = true
                }

            }else {
                if (username.isEmpty()) {
                    inputUsername.setError("Username must be filled with Text")
                    checkLogin = false
                }else if (username != "admin") {
                    inputUsername.setError("Username false")
                    checkLogin = false
                }

                if (password.isEmpty()) {
                    inputPassword.setError("Password must ben filled with text")
                    checkLogin = false
                }else if (password != "admin") {
                    inputPassword.setError("Password false")
                    checkLogin = false
                }

                if (username == "admin" && password == "admin") {
                    checkLogin = true
                }
            }


            if (!checkLogin) return@OnClickListener
            val moveHome = Intent(this@MainActivity, HomeActivity::class.java)
            moveHome.putExtra("User", mBundle)
            startActivity(moveHome)
        })

        // Move ke Activity Register
        btnRegister.setOnClickListener {
            val moveHome = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(moveHome)
        }
    }

    fun getBundle() {
        mBundle = intent.getBundleExtra("register")!!
        newUsername = mBundle.getString("username")!!
        newPassword = mBundle.getString("password")!!
        newEmail = mBundle.getString("email")!!
        newTanggalLahir = mBundle.getString("tanggalLahir")!!
        newNoTelepon = mBundle.getString("noTelepon")!!
    }


    fun setText() {
        inputUsername = findViewById(R.id.inputLayoutUsername)
        inputUsername.getEditText()?.setText(newUsername)
    }
}