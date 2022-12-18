package com.example.toko

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toko.databinding.FragmentHomeBinding
import com.example.toko.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.fragment_home.*

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_home.xml dengan fragment ini
        _binding = FragmentHomeBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrCode.setOnClickListener() {
            val intent = Intent(context, QrCodeActivity::class.java)
            startActivity(intent)
        }

        lokasi_toko.setOnClickListener {
            val supportFragment = FragmentLokasi()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(this.id, supportFragment)
                .addToBackStack("ok")
                .commit()
        }
    }
}