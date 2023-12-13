package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            if (getJwt().toString()!=""){
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this,"자동 로그인 성공", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
            else {
                val intent= Intent(this, LoginActivity::class.java)
                Toast.makeText(this,"로그인 화면으로 돌아갑니다", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
        },3000)
    }
    private fun getJwt():String?{
        val spf=this.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getString("jwt","")
    }
}

