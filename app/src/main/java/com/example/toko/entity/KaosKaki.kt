package com.example.toko.entity

class KaosKaki(var nama: String, var warna: String) {
    companion object{
        @JvmField
        var listOfKaosKaki = arrayOf(
            KaosKaki("MUNDO Classic Casual", "Putih"),
            KaosKaki("Specs Integral 904271", "Hijau"),
            KaosKaki("Nike Classic Dri-Fit Football","Kuning"),
            KaosKaki("AGF Playground Navy Socks", "Merah"),
            KaosKaki("Miniso Low-Cut Breathable Woman", "Pink"),
            KaosKaki("AURA Kaos kaki Wudhu", "Putih"),
            KaosKaki("Robert Brown 9230", "Coklat"),
            KaosKaki("Naughty WSK191200314 Flat", "Staff"),
            KaosKaki("Eiger Hurricane Socks", "Ocan White"),
            KaosKaki("Pierre Cardin PC2 Business", "Blue"),
        )
    }
}