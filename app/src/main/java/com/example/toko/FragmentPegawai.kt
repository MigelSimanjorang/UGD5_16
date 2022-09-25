package com.example.toko

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toko.entity.Pegawai

class FragmentPegawai : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_pegawai.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_pegawai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        val adapter : RVPegawaiAdapter = RVPegawaiAdapter(Pegawai.listOfPegawai)

        // Menghubungkan rvPegawai dengan recycler view yang ada pada layout
        val rvPegawai : RecyclerView = view.findViewById(R.id.rv_pegawai)

        rvPegawai.layoutManager = layoutManager

        // tidak mengubah size recycler view jika terdapat item ditambahkan atau dikurangkan
        rvPegawai.setHasFixedSize(true)

        // Set Adapter dari recycler view
        rvPegawai.adapter = adapter
    }
}