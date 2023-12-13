package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), LoginView {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginSignInBtn.setOnClickListener {
            login()
        }
    }
    private fun login(){
        if (binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()){
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
        }
        if (binding.loginIdEt.text.toString().isEmpty()){
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
        }

        val email:String=binding.loginIdEt.text.toString()+"@"+binding.loginDirectInputEt.text.toString()
        val password:String=binding.loginPasswordEt.text.toString()

//        val songDB=SongDatabase.getInstance(this)!!
//        val user=songDB.userDao().getUser(email, pwd)
//
//        user?.let{
//            Log.d("LOGIN_ACT/GET_USER","userId: ${user.id}, $user")
////            savejwt(user.id)
//            startMainActivity()
//        }

        val authService=AuthService()
        authService.setLoginView(this)

        authService.login(User(email, password, ""))

//        Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
    }

//    private fun savejwt(jwt:Int){
//        val spf=getSharedPreferences("auth", MODE_PRIVATE)
//        val editor=spf.edit()
//
//        editor.putInt("jwt",jwt)
//        editor.apply()
//    }

    private fun savejwt2(jwt:String){
        val spf=getSharedPreferences("auth2", MODE_PRIVATE)
        val editor=spf.edit()

        editor.putString("jwt",jwt)
        editor.apply()

        Log.d("JWT_SEND", jwt)
    }

    private fun startMainActivity(){
        val intent=Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onLoginSuccess(code:Int, result: Result) {
        when(code){
            1000->{
                savejwt2(result.jwt)
                startMainActivity()
            }
        }
    }

    override fun onLoginFailure() {
        //실패 처리
    }

}