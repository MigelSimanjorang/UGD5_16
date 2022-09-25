package com.example.toko.entity

class Pegawai(var nama: String, var role: String, var umur: Int) {
    companion object{
        @JvmField
        var listOfPegawai = arrayOf(
            Pegawai("Putri Selistia", "Manager",20),
            Pegawai("Budi Agung", "Financial",23),
            Pegawai("Rizky Febyun","Building Service",22),
            Pegawai("Zara Araka", "Staff",20),
            Pegawai("M. Sudirman Pamuja", "Staff",21),
            Pegawai("Luluk Mawar", "Staff",25),
            Pegawai("Hariadi", "Staff",21),
            Pegawai("Fedrik Karmawan", "Staff",21),
            Pegawai("Gunawan Adi", "Staff",20),
            Pegawai("Sarah Melody", "Staff",19),
        )
    }
}