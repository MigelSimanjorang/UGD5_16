package com.example.toko

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.toko.databinding.ActivityMainBinding
import com.example.toko.room.SepatuDB
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }
    private lateinit var binding: ActivityMainBinding
    lateinit var  mBundle: Bundle

    private val CHANNEL_ID_LOGIN = "channel_notification_02"
    private val notificationId2 = 102
    private val myPreference = "login"
    private val key = "nameKey"
    private val id = "idKey"
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        val viewBinding = binding.root
        val moveHome = Intent(this@MainActivity, HomeActivity::class.java)

        if(!sharedPreferences!!.contains(key)){
            val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
            editor.putString(key, "terisi")
            editor.apply()
            setContentView(R.layout.activity_splash)

            Handler(Looper.getMainLooper()).postDelayed({
                setContentView(viewBinding)
            }, 3000)
        }else{
            setContentView(viewBinding)
        }

        if (intent.hasExtra("register")) {
            mBundle = intent.getBundleExtra("register")!!
            inputUsername.setText(mBundle.getString("username"))
            inputPassword.setText(mBundle.getString("password"))
        }

        btnLogin.setOnClickListener(View.OnClickListener {
            var checkLogin = false

            CoroutineScope(Dispatchers.IO).launch {
                val users = db.userDao().getUser()
                Log.d("MainActivity ","dbResponse: $users")

                for(i in users){
                    if(inputUsername.text.toString() == i.username && inputPassword.text.toString() == i.password){
                        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                        editor.putString(id, i.id.toString())
                        editor.apply()
                        checkLogin=true
                        break
                    }
                }

                withContext(Dispatchers.Main){
                    if((inputUsername.text.toString() == "admin" && inputPassword.text.toString() == "admin") || (checkLogin)){
                        checkLogin = false
                        startActivity(moveHome)
                        finish()
                    }else {
                        if (inputLayoutUsername.getEditText()?.getText().toString().isEmpty()) {
                            inputLayoutUsername.setError("Username must be filled with Text")
                        }else if (inputLayoutUsername.getEditText()?.getText().toString() != "admin") {
                            inputLayoutUsername.setError("Username false")
                        }

                        if (inputLayoutPassword.getEditText()?.getText().toString().isEmpty()) {
                            inputLayoutPassword.setError("Password must ben filled with text")
                        }else if (inputLayoutPassword.getEditText()?.getText().toString() != "admin") {
                            inputLayoutPassword.setError("Password false")
                        }
                    }
                }
            }

            createNotificationChannel()
            sendNotification1(binding.inputLayoutUsername.getEditText()?.getText().toString())
        })

        btnRegister.setOnClickListener {
            val moveHome = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(moveHome)
        }
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