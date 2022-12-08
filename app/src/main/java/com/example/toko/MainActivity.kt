package com.example.toko

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.api.SepatuApi
import com.example.toko.databinding.ActivityMainBinding
import com.example.toko.models.Login
import com.example.toko.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var  mBundle: Bundle
    private var queue: RequestQueue? = null

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
        Timber.plant(Timber.DebugTree())

        val viewBinding = binding.root
        queue = Volley.newRequestQueue(this)

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

//        if (intent.hasExtra("register")) {
//            mBundle = intent.getBundleExtra("register")!!
//            inputUsername.setText(mBundle.getString("username"))
//            inputPassword.setText(mBundle.getString("password"))
//        }

        binding.btnLogin.setOnClickListener {
            var checkLogin = false

            if (binding.inputLayoutUsername.getEditText()?.getText().toString().isEmpty() || binding.inputLayoutPassword.getEditText()?.getText().toString().isEmpty()) {
                if (inputLayoutUsername.getEditText()?.getText().toString().isEmpty()) {
                    inputLayoutUsername.setError("Username must be filled with Text")
                    Timber.tag("Username").d("Username Kosong")
                }

                if (inputLayoutPassword.getEditText()?.getText().toString().isEmpty()) {
                    inputLayoutPassword.setError("Password must ben filled with text")
                    Timber.tag("Password").d("Password Kosong")
                }
            }else {
                checkLogin = true
            }

            if (!checkLogin) return@setOnClickListener
            loginUser()
        }

        binding.btnRegister.setOnClickListener {
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


            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    private fun loginUser() {
        if (inputLayoutUsername.getEditText()?.getText().toString().isEmpty()) {
            FancyToast.makeText(this,"Username is Empty !",FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else if (inputLayoutPassword.getEditText()?.getText().toString().isEmpty()) {
            FancyToast.makeText(this,"Password is Empty !",FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
        }
        else {
            val login = Login(
                binding.inputLayoutUsername.getEditText()?.getText().toString(),
                binding.inputLayoutPassword.getEditText()?.getText().toString(),
            )
            val stringRequest: StringRequest =
                object: StringRequest(Method.POST, SepatuApi.login, Response.Listener { response ->
                    val gson = Gson()
                    var login = gson.fromJson(response, User::class.java)

                    val jsonObject = JSONObject(response)
                    if(login != null)
                        FancyToast.makeText(this,"Login Success !",FancyToast.LENGTH_LONG, FancyToast.SUCCESS,true).show()

                    val prefEdit : SharedPreferences.Editor = sharedPreferences!!.edit()
                    prefEdit.putInt("id", jsonObject.getJSONObject("user").getInt("id"))
                    prefEdit.apply()

                    val moveHome = Intent(this@MainActivity, HomeActivity::class.java)

                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ez)
                    createNotificationChannel()
                    startActivity(moveHome)
                    finish()

                }, Response.ErrorListener { error ->
                    try{
                        val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        Toast.makeText(
                            this,
                            errors.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
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
                        val requestBody = gson.toJson(login)
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