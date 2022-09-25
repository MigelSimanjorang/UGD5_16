package com.example.toko.entity

import com.example.toko.R

class Barang(var namaSepatu: String, var HargaSepatu: String, var stok: Int, var gambar:Int) {
    companion object{
        @JvmField
        var listOfBarang = arrayOf(
            Barang("Converse x Virgil Abloh","Rp, 600000 ",10, R.drawable.sepatu1),
            Barang("Vans Slip On Classic", "Rp, 500000",15, R.drawable.sepatu2),
            Barang("Superga Cotu Sneakers", "Rp, 400000", 12, R.drawable.sepatu3),
            Barang("League New Kreate Chukka", "Rp, 450000", 10,R.drawable.sepatu4),
            Barang("Casual Piero Shadow P20608", "Rp, 500000", 13,R.drawable.sepatu5),
            Barang("REEBOK Original Instalite", "Rp, 350000", 14, R.drawable.sepatu6),
            Barang("BRODO Signore Boots", "Rp, 400000", 15, R.drawable.sepatu7),
            Barang("Sneaker Suede Ignite", "Rp, 550000", 12, R.drawable.sepatu8),
            Barang("Anvil Royal Brown", "Rp, 600000",10, R.drawable.sepatu9),
            Barang("Aztrek Chalk Royal Rose", "Rp, 550000",10, R.drawable.sepatu10),
        )
    }
}