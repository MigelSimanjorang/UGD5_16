package com.example.toko

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isEmpty
import com.example.toko.databinding.ActivityRegisterBinding
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

    private lateinit var binding: ActivityRegisterBinding
    private val CHANNEL_ID_REGISTER = "channel_notification_01"
    private val notificationId1 = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val viewBinding = binding.root
        setContentView(viewBinding)
        var checkLogin = true

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

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ez)
            createNotificationChannel()
            sendNotification1(inputUsername.text.toString(),Bitmap.createScaledBitmap(bitmap,300,100,false))

            startActivity(moveHome)
        }

    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Register"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_REGISTER,name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }


            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification1(username: String, bitmap : Bitmap){
        val intent : Intent = Intent (this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val broadcastIntent : Intent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage","Hello " + username + ", Your Registration Success !")
        val actionIntent = PendingIntent.getBroadcast(this,0,broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(this, CHANNEL_ID_REGISTER)
            .setSmallIcon(R.drawable.ic_baseline_arrow_back_24)
            .setContentTitle("Registration Success")
            .setContentText("Hello " + username + ", Your Registration Success !")
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))



        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }
}