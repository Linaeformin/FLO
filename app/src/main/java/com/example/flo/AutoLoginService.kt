package com.example.flo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoLoginService {
    private lateinit var autoLoginView: AutoLoginView

//    fun setSignUpView(signUpView: SignUpView){
//        this.signUpView=signUpView
//    }
    fun setAutoLoginView(autoLoginView: AutoLoginView){
        this.autoLoginView=autoLoginView
    }

    fun autologin(user:User){
        val autoLoginService= getRetrofit().create(AutoLoginRetrofitInterface::class.java)
        autoLoginService.autologin(user).enqueue(object: Callback<AutoLoginResponse> {

            override fun onResponse(
                call: Call<AutoLoginResponse>,
                response: Response<AutoLoginResponse>
            ) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                val resp: AutoLoginResponse=response.body()!!
                when(val code=resp.code){
                    1000->autoLoginView.onAutoLoginSuccess(code)
                    else->autoLoginView.onAutoLoginFailure(code)
                }
            }

            override fun onFailure(call: Call<AutoLoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        Log.d("SIGNUP","HELLO")
    }
}