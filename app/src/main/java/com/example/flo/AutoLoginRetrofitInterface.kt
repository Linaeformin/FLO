package com.example.flo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AutoLoginRetrofitInterface {
    @GET("/users")
    fun autoSignUp(@Body user:User): Call<AutoLoginResponse>

    @GET("/users/auto-login")
    fun autologin(@Body user: User): Call<AutoLoginResponse>
}