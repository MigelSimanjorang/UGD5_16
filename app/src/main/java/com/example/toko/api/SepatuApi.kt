package com.example.toko.api

class SepatuApi {
    companion object {
        val BASE_URL = "http://10.113.53.53/ProjectApiVolley/project_api_volley/public/api/"

        val register = BASE_URL + "register"
        val login = BASE_URL + "login"
        val getUserById = BASE_URL + "user/"
        val updateUser = BASE_URL + "user/"

        val GET_ALL_PESANAN = BASE_URL + "pesanan"
        val GET_BY_ID_PESANAN = BASE_URL + "pesanan/"
        val ADD_PESANAN = BASE_URL + "pesanan"
        val UPDATE_PESANAN = BASE_URL + "pesanan/"
        val DELETE_PESANAN = BASE_URL + "pesanan/"

        val GET_ALL_SEPATU = BASE_URL + "sepatu"
        val GET_BY_ID_SEPATU = BASE_URL + "sepatu/"
        val ADD_SEPATU = BASE_URL + "sepatu"
        val UPDATE_SEPATU = BASE_URL + "sepatu/"
        val DELETE_SEPATU = BASE_URL + "sepatu/"

        val GET_ALL_KAOSKAKI = BASE_URL + "kaosKaki"
        val GET_BY_ID_KAOSKAKI = BASE_URL + "kaosKaki/"
        val ADD_KAOSKAKI = BASE_URL + "kaosKaki"
        val UPDATE_KAOSKAKI = BASE_URL + "kaosKaki/"
        val DELETE_KAOSKAKI = BASE_URL + "kaosKaki/"
    }
}