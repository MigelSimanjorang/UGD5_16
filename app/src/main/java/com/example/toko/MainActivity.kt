package com.example.toko

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private val CHANNEL_ID_LOGIN = "channel_notification_02"
    private val notificationId2 = 102

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
            createNotificationChannel()
            sendNotification1(username)
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

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Login"
            val descriptionText = "Login Description"

            val channel1 = NotificationChannel(CHANNEL_ID_LOGIN,name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }


            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification1(username: String) {
        val intent: Intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val broadcastIntent: Intent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra(
            "toastMessage",
            "Hello " + username + ", Your Registration Success !"
        )
        val actionIntent =
            PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_LOGIN)
            .setSmallIcon(R.drawable.ic_baseline_arrow_back_24)
            .setContentTitle("Login Success")
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Login anda telah berhasil! Harap jaga keamanan akun anda. Hati-hati terhadap segala bentuk penipuan karena kami tidak bertanggung jawab atas keamanan akun anda sendiri! Salam sehat dan selamat berbelanja :). Salam hangat dari kami."))



        with(NotificationManagerCompat.from(this)) {
            notify(notificationId2, builder.build())
        }
    }
}