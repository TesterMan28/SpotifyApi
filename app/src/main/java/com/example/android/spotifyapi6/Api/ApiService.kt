package com.example.android.spotifyapi6.Api

interface ApiService {
    companion object {
        val BASE_URL = "http://extenview.com/"
        val LOGIN_URL = "/login"
        val REGISTER_URL = "/register"
    }
    suspend fun loginRequest(
        username: String, password: String
    )
}