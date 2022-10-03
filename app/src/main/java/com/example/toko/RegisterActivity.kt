package com.example.toko

import android.app.DatePickerDialog
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.toko.databinding.ActivityRegisterBinding
import com.example.toko.room.SepatuDB
import com.example.toko.room.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RegisterActivity : AppCompatActivity() {
    val db by lazy { SepatuDB(this) }
    private var userId: Int = 0
    private lateinit var binding: ActivityRegisterBinding
    private val CHANNEL_ID_REGISTER = "channel_notification_01"
    private val notificationId1 = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getSupportActionBar()?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val viewBinding = binding.root
        setContentView(viewBinding)

        val calendar = Calendar.getInstance()
        val tahun = calendar.get(Calendar.YEAR)
        val bulan = calendar.get(Calendar.MONTH)
        val hari = calendar.get(Calendar.DAY_OF_MONTH)

        binding.btnClear.setOnClickListener {
            binding.inputRegisterUsername.setText("")
            binding.inputRegisterPassword.setText("")
            binding.inputRegisterEmail.setText("")
            binding.inputRegisterTanggalLahir.setText("")
            binding.inputRegisterNoTelepon.setText("")

            Snackbar.make(binding.mainLayout, "Text Cleared Success", Snackbar.LENGTH_LONG).show()
        }

        binding.inputRegisterTanggalLahir.setOnFocusChangeListener { view, b ->
            val datePicker =
                this?.let { it1 ->
                    DatePickerDialog(it1, DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                        binding.inputRegisterTanggalLahir.setText("${dayOfMonth}/${(month.toInt() + 1).toString()}/${year}")
                    }, tahun, bulan, hari)
                }
            if(b){
                datePicker?.show()
            }else{
                datePicker?.hide()
            }
        }

        binding.btnRegister.setOnClickListener {
            var checkRegis = false
            val moveHome = Intent(this, MainActivity::class.java)

            if (binding.inputRegisterUsername.text.toString().isEmpty() && binding.inputRegisterPassword.text.toString().isEmpty() && binding.inputRegisterEmail.text.toString().isEmpty() && binding.inputRegisterTanggalLahir.text.toString().isEmpty() && binding.inputRegisterNoTelepon.text.toString().isEmpty()) {
                if (binding.inputRegisterUsername.text.toString().isEmpty()) {
                    binding.inputRegisterUsername.setError("Username must be filled with Text")
                }
                if (binding.inputRegisterPassword.text.toString().isEmpty()) {
                    binding.inputRegisterPassword.setError("Password must be filled with Text")
                }
                if (binding.inputRegisterEmail.text.toString().isEmpty()) {
                    binding.inputRegisterEmail.setError("Email must be filled with Text")
                }
                if (binding.inputRegisterTanggalLahir.text.toString().isEmpty()) {
                    binding.inputRegisterTanggalLahir.setError("Tanggal Lahir must be filled with Text")
                }
                if (binding.inputRegisterNoTelepon.text.toString().isEmpty()) {
                    binding.inputRegisterNoTelepon.setError("No Telepon must be filled with Text")
                }
            } else {
                checkRegis = true
            }

            if (!checkRegis) return@setOnClickListener
            val mBundle = Bundle()

            mBundle.putString("username",binding.inputRegisterUsername.text.toString())
            mBundle.putString("password",binding.inputRegisterPassword.text.toString())
            mBundle.putString("email",binding.inputRegisterEmail.text.toString())
            mBundle.putString("tanggalLahir",binding.inputRegisterTanggalLahir.text.toString())
            mBundle.putString("noTelepon",binding.inputRegisterNoTelepon.text.toString())
            moveHome.putExtra("register",mBundle)

            CoroutineScope(Dispatchers.IO).launch {
                db.userDao().addUser(
                    User(0, binding.inputRegisterUsername.text.toString(), binding.inputRegisterPassword.text.toString(), binding.inputRegisterEmail.text.toString(),binding.inputRegisterTanggalLahir.text.toString(),binding.inputRegisterNoTelepon.text.toString())
                )
                finish()
            }
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ez)
            createNotificationChannel()
            sendNotification1(binding.inputRegisterUsername.text.toString(),Bitmap.createScaledBitmap(bitmap,300,100,false))

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