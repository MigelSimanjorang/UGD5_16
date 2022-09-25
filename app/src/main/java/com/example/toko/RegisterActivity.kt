package com.example.toko

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isEmpty
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var inputUsername: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputTanggalLahir: TextInputEditText
    private lateinit var inputNoTelepon: TextInputEditText
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var btnClear: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Menyembunyikan Action Bar
        getSupportActionBar()?.hide()

        inputUsername = findViewById(R.id.inputRegisterUsername)
        inputPassword = findViewById(R.id.inputRegisterPassword)
        inputEmail = findViewById(R.id.inputRegisterEmail)
        inputTanggalLahir = findViewById(R.id.inputRegisterTanggalLahir)
        inputNoTelepon = findViewById(R.id.inputRegisterNoTelepon)

        mainLayout = findViewById(R.id.mainLayout)
        btnClear = findViewById(R.id.btnClear)
        btnRegister = findViewById(R.id.btnRegister)



        // Aksi btnClear ketika di klik
        btnClear.setOnClickListener { // Mengkosongkan Input
            inputUsername.setText("")
            inputPassword.setText("")
            inputEmail.setText("")
            inputTanggalLahir.setText("")
            inputNoTelepon.setText("")

            // Memunculkan SnackBar
            Snackbar.make(mainLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        btnRegister.setOnClickListener {
            var checkRegister = false
            val moveHome = Intent(this, MainActivity::class.java)

            val username: String = inputUsername.text.toString()
            val password: String = inputPassword.text.toString()
            val email: String = inputEmail.text.toString()
            val tanggalLahir: String = inputTanggalLahir.text.toString()
            val noTelepon: String = inputNoTelepon.text.toString()

//          Pengecekan apakah input username kosong
            if (username.isEmpty()) {
                inputUsername.setError("Username must be filled with Text")
                checkRegister = false
            }
            if (password.isEmpty()) {
                inputPassword.setError("Password must be filled with Text")
                checkRegister = false
            }
            if (email.isEmpty()) {
                inputEmail.setError("Email must be filled with Text")
                checkRegister = false
            }
            if (tanggalLahir.isEmpty()) {
                inputTanggalLahir.setError("Tanggal Lahir must be filled with Text")
                checkRegister = false
            }
            if (noTelepon.isEmpty()) {
                inputNoTelepon.setError("No Telepon must be filled with Text")
                checkRegister = false
            }
            else {
                checkRegister = true
            }
            if (!checkRegister) return@setOnClickListener

            val mBundle = Bundle()

            mBundle.putString("username",inputUsername.text.toString())
            mBundle.putString("password",inputPassword.text.toString())
            mBundle.putString("email",inputEmail.text.toString())
            mBundle.putString("tanggalLahir",inputTanggalLahir.text.toString())
            mBundle.putString("noTelepon",inputNoTelepon.text.toString())
            moveHome.putExtra("register",mBundle)

            startActivity(moveHome)
        }
    }
}