package com.example.toko

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var navigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getSupportActionBar()?.hide()

        changeFragment(FragmentHome())
        navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    changeFragment(FragmentHome())
                    true
                }
                R.id.menu_kaos_kaki -> {
                    changeFragment(FragmentKaosKaki())
                    true
                }
                R.id.menu_barang -> {
                    changeFragment(FragmentBarang())
                    true
                }
                R.id.menu_profile -> {
                    changeFragment(FragmentProfile())
                    true
                }

                else -> false
            }
        }
    }

    fun changeFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}