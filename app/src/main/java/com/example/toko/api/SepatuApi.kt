package com.example.toko.api

class SepatuApi {
    companion object {
        val BASE_URL = "http://192.168.12.252/ProjectApiVolley/project_api_volley/public/api/"

        val register = BASE_URL + "register"
        val login = BASE_URL + "login"
        val getUserById = BASE_URL + "user/"
        val updateUser = BASE_URL + "user/"

        val GET_ALL_PESANAN = BASE_URL + "pesanan"
        val GET_BY_ID_PESANAN = BASE_URL + "pesanan/"
        val ADD_PESANAN = BASE_URL + "pesanan"
        val UPDATE_PESANAN = BASE_URL + "pesanan/"
        val DELETE_PESANAN = BASE_URL + "pesanan/"
    }
}