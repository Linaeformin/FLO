package com.example.flo

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName(value="isSuccesss") val isSuccess: Boolean,
    @SerializedName(value="code") val code:Int,
    @SerializedName(value="message") val message:String,
    @SerializedName(value="result") val result: Result?
    //login api와 회원가입 api를 같은 데이터 클래스로 받고 있기 때문에, Result?로 null 처리를 해야함->회원가입 api를 사용할 때 자동으로 null 처리가 되어 사용 가능함
)
data class Result(
    @SerializedName(value="userIdx") var userIdx: Int,
    @SerializedName(value="jwt") var jwt: String)
