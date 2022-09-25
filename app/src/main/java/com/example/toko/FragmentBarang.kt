package com.example.toko

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toko.entity.Barang


class FragmentBarang : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Proses menghubungkan layout fragment_barang.xml dengan fragment ini
        return inflater.inflate(R.layout.fragment_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        val adapter: RVBarangAdapter = RVBarangAdapter(Barang.listOfBarang)

        // Menghubungkan rvBarang dengan recycler view yang ada pada layout
        val rvBarang: RecyclerView = view.findViewById(R.id.rv_barang)

        rvBarang.layoutManager = layoutManager

        // tidak mengubah size recycler view jika terdapat item ditambahkan atau dikurangkan
        rvBarang.setHasFixedSize(true)

        // Set Adapter dari recycler view
        rvBarang.adapter = adapter
    }
}