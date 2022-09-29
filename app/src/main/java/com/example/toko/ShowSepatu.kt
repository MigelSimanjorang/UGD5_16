package com.example.toko

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toko.room.Constant
import com.example.toko.room.User
import com.example.toko.room.UserDB
import kotlinx.android.synthetic.main.activity_list_sepatu_adapter .*
import kotlinx.android.synthetic.main.activity_show_sepatu.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowSepatu : AppCompatActivity() {
    val db by lazy { UserDB(this) }
    lateinit var noteAdapter: ListSepatuAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_sepatu)
        setupListener()
        setupRecyclerView()
    }
    //berfungsi untuk membuat sebuah note status pada button yang ditekan untuk CRUD yang dilaksanakan
    //ini berhubungan dengan Constant status pada room
    //cara panggil id dengan memanggil fungsi intetnEdit.
    //jika pada fungsi interface adapterListener berubah, maka object akan memerah error karena penambahan fungsi.
    private fun setupRecyclerView() {
        noteAdapter = ListSepatuAdapter(arrayListOf(), object :
            ListSepatuAdapter.OnAdapterListener{
            override fun onClick(note: User) {
                Toast.makeText(applicationContext, note.title, Toast.LENGTH_SHORT).show()
                intentEdit(note.id,Constant.TYPE_READ)
            }
            override fun onUpdate(note: User) {
                intentEdit(note.id, Constant.TYPE_UPDATE)
            }
            override fun onDelete(note: User) {
                deleteDialog(note)
            }
        })
        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }
    private fun deleteDialog(note: User){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirmation")
            setMessage("Are You Sure to delete this data From ${note.title}?")
            setNegativeButton("Cancel", DialogInterface.OnClickListener
            { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            setPositiveButton("Delete", DialogInterface.OnClickListener
            { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.userDao().deleteUser(note)
                    loadData()
                }
            })
        }
        alertDialog.show()
    }
    override fun onStart() {
        super.onStart()
        loadData()
    }
    //untuk load data yang tersimpan pada database yang sudah create data
    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.userDao().getUser()
            Log.d("ShowFamily","dbResponse: $notes")
            withContext(Dispatchers.Main){
                noteAdapter.setData(notes)
            }
        }
    }
    fun setupListener() {
        button_create.setOnClickListener{
            intentEdit(0, Constant.TYPE_CREATE)
        }
    }
    //pick data dari Id yang sebagai primary key
    fun intentEdit(noteId : Int, intentType: Int){
        startActivity(
            Intent(applicationContext, EditSepatu::class.java)
                .putExtra("intent_id", noteId)
                .putExtra("intent_type", intentType)
        )
    }
}