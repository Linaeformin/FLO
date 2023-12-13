package com.example.flo

import com.google.gson.annotations.SerializedName

data class AutoLoginResponse(
    @SerializedName(value="isSuccesss") val isSuccess: Boolean,
    @SerializedName(value="code") val code:Int,
    @SerializedName(value="message") val message:String,
)

