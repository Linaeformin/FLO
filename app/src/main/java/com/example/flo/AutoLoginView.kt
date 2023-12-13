package com.example.flo

interface AutoLoginView {
    fun onAutoLoginSuccess(code:Int)
    fun onAutoLoginFailure(code:Int)
}