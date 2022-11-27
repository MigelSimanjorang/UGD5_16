package com.example.toko

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.toko.api.SepatuApi
import com.example.toko.databinding.ActivityRegisterBinding
import com.example.toko.room.SepatuDB
//import com.example.toko.room.User
import com.example.toko.models.User
import com.example.toko.room.Buy
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.android.synthetic.main.activity_tambah_pesanan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

import android.view.View

//import com.itextpdf.barcodes.BarcodeQRCode
//import com.itextpdf.io.image.ImageDataFactory
//import com.itextpdf.io.source.ByteArrayOutputStream
//import com.itextpdf.kernel.colors.ColorConstants
//import com.itextpdf.kernel.geom.PageSize
//import com.itextpdf.kernel.pdf.PdfDocument
//import com.itextpdf.kernel.pdf.PdfWriter
//import com.itextpdf.layout.Document
//import com.itextpdf.layout.element.Cell
//import com.itextpdf.layout.element.Image
//import com.itextpdf.layout.element.Paragraph
//import com.itextpdf.layout.element.Table
//import com.itextpdf.layout.property.HorizontalAlignment
//import com.itextpdf.layout.property.TextAlignment

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val CHANNEL_ID_REGISTER = "channel_notification_01"
    private val notificationId1 = 101
    private var queue: RequestQueue? = null

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
        queue = Volley.newRequestQueue(this)

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
            registerUser()
        }
    }

    private fun pdf() {
//        val username = binding!!.inputRegisterUsername.text.toString()
//        val email = binding!!.inputRegisterEmail.text.toString()
//        val tanggalLahir = binding!!.inputRegisterTanggalLahir.text.toString()
//        val noTelepon = binding!!.inputRegisterNoTelepon.text.toString()
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (username.isEmpty() && email.isEmpty() && tanggalLahir.isEmpty() && noTelepon.isEmpty()) {
//                    FancyToast.makeText(this,"Login Success !",
//                        FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show()
//                }else {
//                    createPdf(username, email, tanggalLahir, noTelepon)
//                }
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
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

    private fun registerUser() {
        val register = User(
            binding.inputRegisterUsername.text.toString(),
            binding.inputRegisterPassword.text.toString(),
            binding.inputRegisterEmail.text.toString(),
            binding.inputRegisterTanggalLahir.text.toString(),
            binding.inputRegisterNoTelepon.text.toString(),
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, SepatuApi.register, Response.Listener { response ->
                val gson = Gson()
                var register = gson.fromJson(response, User::class.java)

                if(register != null)
                    Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()

                val mBundle = Bundle()
                val moveMain = Intent(this, MainActivity::class.java)

                mBundle.putString("username",binding.inputRegisterUsername.text.toString())
                mBundle.putString("password",binding.inputRegisterPassword.text.toString())
                mBundle.putString("email",binding.inputRegisterEmail.text.toString())
                mBundle.putString("tanggalLahir",binding.inputRegisterTanggalLahir.text.toString())
                mBundle.putString("noTelepon",binding.inputRegisterNoTelepon.text.toString())
                moveMain.putExtra("register",mBundle)

                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ez)
                createNotificationChannel()
                sendNotification1(binding.inputRegisterUsername.text.toString(),Bitmap.createScaledBitmap(bitmap,300,100,false))
                pdf()
                startActivity(moveMain)
                finish()

//                setLoading(false)
            }, Response.ErrorListener { error ->
//                setLoading(false)
                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception){
                    Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_SHORT).show()
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
                    val requestBody = gson.toJson(register)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }

        queue!!.add(stringRequest)
    }

//    @SuppressLint("ObsoleteSdkInt")
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Throws(
//        FileNotFoundException::class
//    )
//    private fun createPdf(username: String, email: String, tanggalLahir: String, noTelepon: String) {
//        //ini berguna untuk akses Writing ke Storage HP dalam mode Download.
//        val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//        val file = File(pdfPath, "pdf_register_kushoes.pdf")
//        FileOutputStream(file)
//
//        //inisaliasi pembuatan PDF
//        val writer = PdfWriter(file)
//        val pdfDocument = PdfDocument(writer)
//        val document = Document(pdfDocument)
//        pdfDocument.defaultPageSize = PageSize.A4
//        document.setMargins(5f, 5f, 5f, 5f)
//        @SuppressLint("UseCompatLoadingForDrawables") val d = getDrawable(R.drawable.pdf_background)
//
//        //penambahan gambar pada Gambar atas
//        val bitmap = (d as BitmapDrawable?)!!.bitmap
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//        val bitmapData = stream.toByteArray()
//        val imageData = ImageDataFactory.create(bitmapData)
//        val image = Image(imageData)
//        val namapengguna = Paragraph("Selamat Datang di Aplikasi KuShoes").setBold().setFontSize(24f)
//            .setTextAlignment(TextAlignment.CENTER)
//        val group = Paragraph(
//                        """
//                        Berikut adalah
//                        Data Registrasi Anda
//                        """.trimIndent()).setTextAlignment(TextAlignment.CENTER).setFontSize(12f)
//
//        //proses pembuatan table
//        val width = floatArrayOf(100f, 100f)
//        val table = Table(width)
//        //pengisian table dengan data-data
//        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
//        table.addCell(Cell().add(Paragraph("Username")))
//        table.addCell(Cell().add(Paragraph(username)))
//        table.addCell(Cell().add(Paragraph("Email")))
//        table.addCell(Cell().add(Paragraph(email)))
//        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//        table.addCell(Cell().add(Paragraph("Tanggal Lahir")))
//        table.addCell(Cell().add(Paragraph(LocalDate.now().format(dateTimeFormatter))))
//        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a")
//        table.addCell(Cell().add(Paragraph(email)))
//        table.addCell(Cell().add(Paragraph("Nomor Telepon")))
//        table.addCell(Cell().add(Paragraph(noTelepon)))
//        table.addCell(Cell().add(Paragraph("Pukul Pembuatan")))
//        table.addCell(Cell().add(Paragraph(LocalTime.now().format(timeFormatter))))
//
//        //pembuatan QR CODE secara generate dengan bantuan IText7
//        val barcodeQRCode = BarcodeQRCode(
//                                        """
//                                        $username
//                                        $email
//                                        $tanggalLahir
//                                        $noTelepon
//                                        ${LocalDate.now().format(dateTimeFormatter)}
//                                        ${LocalTime.now().format(timeFormatter)}
//                                        """.trimIndent())
//
//        val qrCodeObject = barcodeQRCode.createFormXObject(ColorConstants.BLACK, pdfDocument)
//        val qrCodeImage = Image(qrCodeObject).setWidth(80f).setHorizontalAlignment(HorizontalAlignment.CENTER)
//
//        document.add(image)
//        document.add(namapengguna)
//        document.add(group)
//        document.add(table)
//        document.add(qrCodeImage)
//
//        document.close()
//        Toast.makeText(this, "Pdf Created", Toast.LENGTH_LONG).show()
//    }
}