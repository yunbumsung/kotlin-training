package com.test.edu.kotlin_fb_demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.test.edu.kotlin_fb_demo.ui.RootActivity
import com.test.edu.kotlin_fb_demo.ui.SignInActivity

class MainActivity : RootActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 인증을 담당하는 액티비티로 이동
        startActivity( Intent(this, SignInActivity::class.java) )
        finish()
        //showProgressDialog()
    }
    // 백키를 누르면 앱이 종료되는데, 이 이벤트를 인터셉트
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
